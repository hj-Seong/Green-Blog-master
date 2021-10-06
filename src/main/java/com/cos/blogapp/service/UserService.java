package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotfoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;

	//핵심로직에서!
	//이건 하나의 서비스인가?(principal 값 변경(길면x/짧으면o), update치고, 세션값 변경(x))
	
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 유저정보수정(UserSaveReqDto dto, User principal) {
		// 핵심로직
		User userEntity = userRepository.findById(principal.getId())
				.orElseThrow(()->new MyAsyncNotFoundException("회원정보를 찾을수 없습니다."));
			
		userEntity.setEmail(dto.getEmail());
		//더티체킹
	}

	public User 로그인(LoginReqDto dto) {
		String encPassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		User userEntity = userRepository.mLogin(dto.getUsername(), encPassword);
		return userEntity;
	}

	@Transactional(rollbackFor = MyNotfoundException.class)
	public void 회원가입(JoinReqDto dto) {
		// 프로그램은 누구나싫수할수있다. 실수하지 않게 만들어줘야한다.
		String encPassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassword);

		userRepository.save(dto.toEntity());

	}

}
