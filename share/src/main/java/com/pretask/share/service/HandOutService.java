package com.pretask.share.service;

import static com.pretask.share.entity.QHandOutDetail.handOutDetail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.pretask.share.cacheDto.HandOutCache;
import com.pretask.share.cacheRepository.HandOutCacheRepository;
import com.pretask.share.dto.HandOutDto;
import com.pretask.share.dto.HandOutRegDto;
import com.pretask.share.dto.HandOutSearchDetailDto;
import com.pretask.share.dto.HandOutSearchDto;
import com.pretask.share.dto.QHandOutSearchDetailDto;
import com.pretask.share.entity.HandOut;
import com.pretask.share.entity.HandOutDetail;
import com.pretask.share.entity.Member;
import com.pretask.share.exception.NoSearchRoomException;
import com.pretask.share.exception.NoSearchUserException;
import com.pretask.share.exception.NotFoundHandOutException;
import com.pretask.share.repository.HandOutDetailRepository;
import com.pretask.share.repository.HandOutRepository;
import com.pretask.share.repository.MemberRepository;
import com.pretask.share.repository.RoomRepository;
import com.pretask.share.util.TokenUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service
@Transactional
public class HandOutService {

	@Autowired
	private HandOutRepository handOutRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private HandOutDetailRepository handOutDetailRepository;
	
	@Autowired
	private HandOutCacheRepository handOutCacheRepository;
	
	@Resource(name = "redisTemplate")
	private ListOperations<String, Long> listOperations;
	
	@Resource(name = "redisTemplate")
	private RedisTemplate<String, Long> redisTemplate;
	
	@Autowired
	EntityManager em;
	
	private JPAQueryFactory queryFactory;
	
	private final Duration receiveTimeToLive = Duration.ofSeconds(10);
	private final Duration searchTimeToLive = Duration.ofDays(7);
	private final String HAND_OUT_CACHE = "handOutCache:";
	
	public HandOut handOutReg(HandOutRegDto regDto, String room_id, long user_id) throws Exception{
		
		HandOut handOut = new HandOut();
		handOut.setPrice(regDto.getPrice());
		handOut.setCount(regDto.getCount());
		handOut.setHandOutMember(memberRepository.findById(user_id).orElseThrow(NoSearchUserException::new));
		handOut.setRoom(roomRepository.findById(room_id).orElseThrow(NoSearchRoomException::new));

		StringBuilder keyBuilder = new StringBuilder();
		do {
			handOut.setToken(TokenUtil.getInstance().makeToken());
			keyBuilder
			.append(handOut.getToken())
			.append(":")
			.append(handOut.getRoom().getRoom_id());
		}while(handOutCacheRepository.existsById(keyBuilder.toString()));
		
		handOutRepository.save(handOut);
		HandOutCache handOutCache = HandOutCache.builder()
				.id(keyBuilder.toString())
				.handOutMemberId(handOut.getHandOutMember().getMember_id())
				.handOutId(handOut.getId())
				.handOutPrice(handOut.getPrice())
				.handOutDate(handOut.getRegDate())
				.build();
		handOutCacheRepository.save(handOutCache);
		
		List<HandOutDetail> details = new ArrayList<>();
		long price = handOut.getPrice() / handOut.getCount();
		for(int idx = 0; idx < handOut.getCount(); idx++) {
			if(idx == handOut.getCount()-1) {
				price = handOut.getPrice() - ( price * idx );
			}	
			HandOutDetail detail = new HandOutDetail(handOut, price);
			details.add(detail);
		}
		handOutDetailRepository.saveAll(details);
		
		List<Long> list = new ArrayList<Long>();
		for(HandOutDetail detail : details) {
			list.add(detail.getId());
		}
		listOperations.rightPushAll(keyBuilder.toString(), list);
		
		redisTemplate.expire(keyBuilder.toString(), receiveTimeToLive);
		return handOut;
	}
	
	public HandOutDetail handOutReceive(HandOutDto handOutDto, String room_id, long user_id) throws Exception{
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder
			.append(handOutDto.getToken())
			.append(":")
			.append(room_id);
		
		long count = Optional.ofNullable(listOperations.size(keyBuilder.toString())).orElseThrow(NotFoundHandOutException::new);
		if(count == 0) throw new NotFoundHandOutException();
		
		HandOutCache handOutCache = handOutCacheRepository.findById(keyBuilder.toString()).orElseThrow(NotFoundHandOutException::new);
		
		if(user_id == handOutCache.getHandOutMemberId()) throw new Exception("뿌리기 등록자는 받을 수 없습니다.");
		HashSet<Long> receive = handOutCache.getReceiveMemberIds();
		if(receive.contains(user_id)) throw new Exception("뿌리기 당 한번만 수령 가능합니다.");
		else {
			receive.add(user_id);
			handOutCache.setReceiveMemberIds(receive);
			handOutCacheRepository.save(handOutCache);
		}
		
		long handOutDetailId = Optional.ofNullable(listOperations.leftPop(keyBuilder.toString())).orElseThrow(NotFoundHandOutException::new);
		HandOutDetail handOutDetail = handOutDetailRepository.findById(handOutDetailId).orElseThrow(NotFoundHandOutException::new);
		Member receiveMember = memberRepository.findById(user_id).orElseThrow(NoSearchUserException::new);
		handOutDetail.setReceiveMember(receiveMember);
		receiveMember.addPrice(handOutDetail.getReceivePrice());
		
		return handOutDetail;
	}
	
	public HandOutSearchDto handOutSearch(String token, String room_id, long user_id) throws Exception{
		queryFactory = new JPAQueryFactory(em);
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(token).append(":").append(room_id);
		
		HandOutCache handOutCache = handOutCacheRepository.findById(keyBuilder.toString()).orElseThrow(NotFoundHandOutException::new);
		
		if(user_id != handOutCache.getHandOutMemberId()) throw new Exception("뿌리기 등록자만 조회가 가능합니다");
		
		List<HandOutSearchDetailDto> details = queryFactory
				.select(new QHandOutSearchDetailDto(handOutDetail.receiveMember.member_id, handOutDetail.receivePrice))
				.from(handOutDetail)
				.where(handOutDetail.handOut.id.eq(handOutCache.getHandOutId()).and(handOutDetail.receiveMember.isNotNull()))
				.fetch();
		
		long sum = details.stream().map(HandOutSearchDetailDto::getReceivePrice).reduce((a, b) -> a + b).get();
		
		return HandOutSearchDto.builder()
				.handOutTime(handOutCache.getHandOutDate())
				.handOutPrice(handOutCache.getHandOutPrice())
				.receivePrice(sum)
				.details(details)
				.build();
	}
}
