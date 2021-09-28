package com.cos.blogapp.domain.board;

class 사과 {}
class 딸기 {}


 				//제네릭 - 어떤 타입이 들어올지 모를때.
public class 바구니<T> {

	//사과 딸기     
	public void 담기(T t) {}
	//제네릭을 쓸수 없을때는? Object, 모든 클래스는 Obect를 상속받는다. 
	
	public static void main(String[] args) {
		사과 a = new 사과(); //메모리에 생성
		Object b = new 사과(); //메모리에 생성
		//다형성, 다양한 형태로 불릴수 있따.
		
		바구니<사과> s = new 바구니();
		s.담기(a);
		
		바구니<딸기> k = new 바구니();
		
	}
}
