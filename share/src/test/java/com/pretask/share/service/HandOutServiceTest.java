package com.pretask.share.service;

import static com.pretask.share.entity.QHandOutDetail.handOutDetail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pretask.share.dto.HandOutDto;
import com.pretask.share.dto.HandOutRegDto;
import com.pretask.share.dto.HandOutSearchDetailDto;
import com.pretask.share.dto.HandOutSearchDto;
import com.pretask.share.entity.HandOut;
import com.pretask.share.entity.HandOutDetail;
import com.pretask.share.entity.Member;
import com.pretask.share.entity.Room;
import com.pretask.share.exception.NoSearchRoomException;
import com.pretask.share.exception.NoSearchUserException;
import com.pretask.share.exception.NotFoundHandOutException;
import com.pretask.share.repository.HandOutDetailRepository;
import com.pretask.share.repository.HandOutRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
@TestMethodOrder(OrderAnnotation.class)
class HandOutServiceTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	private HandOutService handOutService;
	
	@Autowired
	private HandOutDetailRepository handOutDetailRepository;
	
	@Autowired
	private HandOutRepository handOutRepository;
	
	private JPAQueryFactory queryFactory;
	
	private long member1_id;
	private long member2_id;
	private long member3_id;
	private long member4_id;
	
	@BeforeEach
	void test() {
		queryFactory = new JPAQueryFactory(em);

		Member member1 = new Member("사용자1");
		Member member2 = new Member("사용자2");
		Member member3 = new Member("사용자3");
		Member member4 = new Member("사용자4");
		
		Room room1 = new Room("room_1");
		Room room2 = new Room("room_2");
		member1.joinRoom(room1);
		member2.joinRoom(room1);
		member3.joinRoom(room1);
		member4.joinRoom(room1);
		
		member1.joinRoom(room2);
		member2.joinRoom(room2);
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		em.persist(room1);
		em.persist(room2);
		
		member1_id = member1.getMember_id();
		member2_id = member2.getMember_id();
		member3_id = member3.getMember_id();
		member4_id = member4.getMember_id();
	}
	
	@Test
	void handOutReg_회원정보없음() {
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(5);
		regDto.setPrice(1000);
		assertThrows(NoSearchUserException.class,
				() -> handOutService.handOutReg(regDto, "room_1", 100));
	}
	
	@Test
	void handOutReg_대화방정보없음() {
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(5);
		regDto.setPrice(1000);
		assertThrows(NoSearchRoomException.class,
				() -> handOutService.handOutReg(regDto, "room_0", member1_id));
	}

	@Test
	void handOutReg_금액_및_detail수량_확인() throws Exception {
		//given
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(5);
		regDto.setPrice(984);
		
		//when
		HandOut handOut = handOutService.handOutReg(regDto, "room_1", member1_id);
		List<HandOutDetail> details = queryFactory
		.selectFrom(handOutDetail)
		.where(handOutDetail.handOut.id.eq(handOut.getId()))
		.fetch();
		
		//then
		//뿌리기 수량 검증
		assertEquals(handOut.getCount(), details.size());
		//뿌리기 금액 검증
		assertEquals(handOut.getPrice(), details.stream().map(HandOutDetail::getReceivePrice).reduce((a, b) -> a + b).get());
	}
	
	@Test
	void handOutReceive_수령() throws Exception {
		//given
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(2);
		regDto.setPrice(984);
		
		
		//when
		HandOut handOut = handOutService.handOutReg(regDto, "room_1", member1_id);
		
		//then 등록자 수령으로 인한 Exception
		assertThrows(Exception.class,
				() -> handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member1_id));
		
		//then member2, member3 정상수령
		handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member2_id);
		handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member3_id);
		
		//then member2 2차 수령으로 인한 exception
		assertThrows(Exception.class,
				() -> handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member2_id));
		
		//then 뿌리기 소진으로 인한 exception
		assertThrows(NotFoundHandOutException.class,
				() -> handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member4_id));
	}
	
	@Test
	void handOutSearch_성공() throws Exception {
		//given
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(3);
		regDto.setPrice(986);
		
		HandOut handOut = handOutService.handOutReg(regDto, "room_1", member1_id);
		
		HandOutDetail receive1 = handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member2_id);
		HandOutDetail receive2 = handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member3_id);
		
		HandOutDto handOutDto = new HandOutDto();
		handOutDto.setToken(handOut.getToken());
		
		//when
		HandOutSearchDto handOutSearchDto = handOutService.handOutSearch(handOutDto.getToken(), "room_1", member1_id);
		
		//then
		assertEquals(receive1.getReceivePrice()+receive2.getReceivePrice(), handOutSearchDto.getReceivePrice());
		assertEquals(2, handOutSearchDto.getDetails().size());
		HandOutSearchDetailDto detail = handOutSearchDto.getDetails().get(0);
		if(detail.getReceiveMemberId() == member2_id) {
			assertEquals(receive1.getReceivePrice(), detail.getReceivePrice());
		}else if(detail.getReceiveMemberId() == member3_id) {
			assertEquals(receive2.getReceivePrice(), detail.getReceivePrice());
		}
	}
	
	@Test
	void handOutSearch_등록자_외_조회() throws Exception {
		//given
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(3);
		regDto.setPrice(986);
		
		HandOut handOut = handOutService.handOutReg(regDto, "room_1", member1_id);
		
		handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member2_id);
		handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member3_id);
		
		HandOutDto handOutDto = new HandOutDto();
		handOutDto.setToken(handOut.getToken());
		
		//when then
		assertThrows(Exception.class,
				() -> handOutService.handOutSearch(handOutDto.getToken(), "room_1", member2_id));
	}
	
	@Test
	void handOutReceive_시간만료() throws Exception {
		//given
		HandOutRegDto regDto = new HandOutRegDto();
		regDto.setCount(2);
		regDto.setPrice(984);
		HandOut handOut = handOutService.handOutReg(regDto, "room_1", member1_id);
		
		Thread.sleep(1000);
		//when 정상 수령 및 10초 슬립(테스트 시 10분->10초로 전환)
		handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member3_id);
		Thread.sleep(10000);
		
		//then
		assertThrows(NotFoundHandOutException.class,
				() -> handOutService.handOutReceive(HandOutDto.builder().token(handOut.getToken()).build(), "room_1", member4_id));
	}
}