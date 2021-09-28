package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;


//단위 테스트시 static 뺴기!
public class SHATest {

	//encrypt : 암호화라는 뜻
	@Test
	public void encrypt()  {
		String salt = ""; //안전하게 읽기 위함.
		String rawPassword = "ssar1234";
		
		//1. SHA256 함수를 가진 클래스 객체 가져오기
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256"); //나머지해쉬들으 뚦림
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//getInstance는 new응이용해서 만들수 없고 들고와야한다,
		//try catch / throws 함수내부 전체
		
		//2. 비밀번호 1234 -> ShA256 던지기
		md.update(rawPassword.getBytes());
		
		for (Byte b : md.digest()) {
			System.out.print(b);
		}
		System.out.println();
		StringBuilder sb = new StringBuilder();
		for (Byte b : md.digest()) {
			sb.append(String.format("%02x", b));
		}
		System.out.println(sb.toString()); //16진수로 바꿈
		System.out.println(sb.toString().length());
		
		//return 
	}
}
