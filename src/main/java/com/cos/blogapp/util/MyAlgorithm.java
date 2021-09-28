package com.cos.blogapp.util;

import lombok.Getter;


//열거형 (카테고리 정할 때, 범주가 정해져 있을 때)
//내부적으로 객체가 만듷어진다.
@Getter
public enum MyAlgorithm {
	//괄호에 관한값
	SHA256("SHA-256"), SHA512("SHA-512");
	//만약 '-'를 적을수 있다면,SHA-256, SHA-512 이렇게 가능하다. 
	
	
	//이넘의 설개
	private String type;
	
	private MyAlgorithm(String type) {
		this.type = type;
	}
}
