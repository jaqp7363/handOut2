package com.pretask.share.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDto {

	private String message;
	
	public ExceptionDto(String message) {
		this.message = message;
	}
}
