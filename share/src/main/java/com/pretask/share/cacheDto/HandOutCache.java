package com.pretask.share.cacheDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "handOutCache", timeToLive = 600)
public class HandOutCache {

	@Id
	private String id;
	
	private long handOutMemberId;
	
	private long handOutId;
	
	private long handOutPrice;
	
	private String handOutDate; 
	
	private HashSet<Long> receiveMemberIds = new HashSet<Long>();
	
	@Builder
	public HandOutCache(String id, long handOutMemberId, long handOutId, long handOutPrice, LocalDateTime handOutDate) {
		this.id = id;
		this.handOutMemberId = handOutMemberId;
		this.handOutId = handOutId;
		this.handOutPrice = handOutPrice;
		this.handOutDate = handOutDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
	}
}
