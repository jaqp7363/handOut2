package com.pretask.share.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HandOutSearchDetailDto {

	private long receiveMemberId;
	
	private long receivePrice;
	
	@QueryProjection
	public HandOutSearchDetailDto(long receiveMemberId, long receivePrice) {
		this.receiveMemberId = receiveMemberId;
		this.receivePrice = receivePrice;
	}
}
