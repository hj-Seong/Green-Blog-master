package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {

	//encrypt : 암호화라는 뜻
	public static String encrypt(String rawPassword, MyAlgorithm algorithm) {
		
		//1. SHA256 함수를 가진 클래스 객체 가져오기

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm.getType()); //SHA-256,SHA-512 사람리 실수할수 있음. 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //나머지해쉬들으 뚦림
		//getInstance는 new응이용해서 만들수 없고 들고와야한다,
		//try catch / throws 함수내부 전체
		
		//2. 비밀번호 1234 -> ShA256 던지기
		md.update(rawPassword.getBytes());
		
		// 3.암호화된 글자를 16진수로 변환(헥사코드)
		StringBuilder sb = new StringBuilder();
		for (Byte b : md.digest()) {
			sb.append(String.format("%02x", b));
		}
		System.out.println(sb.toString()); //16진수로 바꿈
		
		return sb.toString();
	}
}
