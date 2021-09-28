package com.cos.blogapp.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotfoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.CMRespDto;


// ControllerAdvice 두가지 일 1. 익셉션 핸들링 2. Controller의 역할 까지한다. 
// 문서에 적혀있다.
@ControllerAdvice
public class GlobalExceptionHandler {

	//일반 - 자바스크립트
	@ExceptionHandler(value = MyNotfoundException.class)
	public @ResponseBody String error1(MyNotfoundException e) {
		System.out.println("오류 :"+e.getMessage());
		return Script.href("/", e.getMessage() );
		
	}
	
	//패치요청(데이터를 응답받아야 할 때) > 안드로이드 응답-안드로이드는 자바스크립트이해못함
	@ExceptionHandler(value = MyAsyncNotFoundException.class)
	public @ResponseBody CMRespDto<String> error2(MyAsyncNotFoundException e) {
		System.out.println("오류 :"+e.getMessage());
		return new CMRespDto<String>(-1, e.getMessage(), null);
		
	}
}

