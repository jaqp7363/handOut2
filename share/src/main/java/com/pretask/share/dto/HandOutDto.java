package com.pretask.share.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class HandOutDto {

	@NotNull
	private String token;
	
	@Builder
	public HandOutDto(String token) {
		this.token = token;
	}
}
