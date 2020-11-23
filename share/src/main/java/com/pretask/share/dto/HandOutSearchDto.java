package com.pretask.share.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HandOutSearchDto {

	private String handOutTime;
	
	private long handOutPrice;
	
	private long receivePrice;
	
	private List<HandOutSearchDetailDto> details = new ArrayList<>();
	
	@Builder
	public HandOutSearchDto(String handOutTime, long handOutPrice, long receivePrice, List<HandOutSearchDetailDto> details) {
		this.handOutTime = handOutTime;
		this.handOutPrice = handOutPrice;
		this.receivePrice = receivePrice;
		this.details = details;
	}
}
