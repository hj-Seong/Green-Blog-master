package com.cos.blogapp.handler.ex;

/**
 * 
 * @author Administrator 본인이름 2021. 09. 16
 * 1. id를 못 찾았을 때 사용
 * 
 */


public class MyNotfoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MyNotfoundException(String msg) {
		super(msg);
	}
}
