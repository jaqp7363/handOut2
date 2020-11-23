package com.pretask.share.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pretask.share.dto.HandOutDto;
import com.pretask.share.dto.HandOutReceiveDto;
import com.pretask.share.dto.HandOutRegDto;
import com.pretask.share.dto.HandOutSearchDto;
import com.pretask.share.service.HandOutService;

@RestController
@RequestMapping("/api/v0")
public class APIController {

	@Autowired
	private HandOutService handOutService;
	
	@PostMapping("/handout")
	public ResponseEntity<HandOutDto> handOut(HttpServletRequest request, @Valid @RequestBody HandOutRegDto regDto) throws Exception{
		HandOutDto handOutDto = new HandOutDto();
		String room_id = request.getHeader("X-ROOM-ID");
		long user_id = Long.parseLong(request.getHeader("X-USER-ID"));
		handOutDto.setToken(handOutService.handOutReg(regDto, room_id, user_id).getToken());
		return new ResponseEntity<HandOutDto>(handOutDto, HttpStatus.OK);
	}
	
	@PutMapping("/handout")
	public ResponseEntity<HandOutReceiveDto> receive(HttpServletRequest request, @Valid @RequestBody HandOutDto handOutDto) throws Exception{
		HandOutReceiveDto dto = new HandOutReceiveDto();
		String room_id = request.getHeader("X-ROOM-ID");
		long user_id = Long.parseLong(request.getHeader("X-USER-ID"));
		dto.setReceivePrice(handOutService.handOutReceive(handOutDto, room_id, user_id).getReceivePrice());
		return new ResponseEntity<HandOutReceiveDto>(dto, HttpStatus.OK);
	}
	
	@GetMapping("/handout/{token}")
	public ResponseEntity<HandOutSearchDto> handOutReg(HttpServletRequest request, @PathVariable("token") String token) throws Exception {
		String room_id = request.getHeader("X-ROOM-ID");
		long user_id = Long.parseLong(request.getHeader("X-USER-ID"));
		if(token == null || token.length() != 3) throw new Exception("잘못된 토큰정보 입니다.");
		HandOutSearchDto dto = handOutService.handOutSearch(token, room_id, user_id);
		return new ResponseEntity<HandOutSearchDto>(dto, HttpStatus.OK);
	}
}
