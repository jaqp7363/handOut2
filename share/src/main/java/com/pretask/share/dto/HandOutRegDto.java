package com.pretask.share.dto;

import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandOutRegDto {

	@Positive(message = "price는 필수 값 입니다. 0보다 커야합니다.")
	private int price;
	
	@Positive(message = "count는 필수 값 입니다. 0보다 커야합니다.")
	private int count;
}
