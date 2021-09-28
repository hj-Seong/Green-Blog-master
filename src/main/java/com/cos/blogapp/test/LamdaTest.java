package com.cos.blogapp.test;


// 1.8 람다식  optional<>  자바 저번에 따라 어떤것들이 생겼는지 봐야한다.
// 1. 함수를 넘기는게 목적
// 2. 인터체이스에 함수가 무조건 하나여야 함
// 3. 쓰면 코드 간결해지고, 타입을 몰라도 됨.
// 매개변수는 넣어줘야한다. 함수보고 판단. 
interface MySupplier {
	void get();
}

public class LamdaTest {
	//문법배우는 중
	static void start(MySupplier s) {
		s.get();
	}
	
	public static void main(String[] args) {
		start(() -> {System.out.println("get함수호출");});
	}
}
