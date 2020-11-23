package com.pretask.share.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pretask.share.dto.ExceptionDto;
import com.pretask.share.exception.ApiHeaderException;
import com.pretask.share.exception.NoSearchRoomException;
import com.pretask.share.exception.NoSearchUserException;
import com.pretask.share.exception.NotFoundHandOutException;

@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ApiHeaderException.class)
	public ResponseEntity<ExceptionDto> ApiHeaderException(Exception e) {
		return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
	
	@ExceptionHandler(NoSearchRoomException.class)
	public ResponseEntity<ExceptionDto> NoSearchRoomException(Exception e) {
		return new ResponseEntity<>(new ExceptionDto("대화방을 찾을 수 없습니다."), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NoSearchUserException.class)
	public ResponseEntity<ExceptionDto> NoSearchUserException(Exception e) {
		return new ResponseEntity<>(new ExceptionDto("회원정보를 찾을 수 없습니다."), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NotFoundHandOutException.class)
	public ResponseEntity<ExceptionDto> NotFoundHandOutException(Exception e) {
		return new ResponseEntity<>(new ExceptionDto("요청하신 뿌리기는 만료되었거나 존재하지 않습니다."), HttpStatus.NOT_FOUND);
	}
}
