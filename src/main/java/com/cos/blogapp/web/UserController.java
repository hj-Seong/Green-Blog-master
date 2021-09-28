package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

	private final UserRepository userRepository;
	private final HttpSession session;

	@PutMapping("/user/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody UserSaveReqDto dto,
			BindingResult bindingResult) {

		// 공통로직 > 안전, 나중에 프레임워크의 기능을 사용해서 처리가능.
		// 인증
		User principal = (User) session.getAttribute("principal");
		if ((principal == null)) {
			// return new CMRespDTO<>(-1, "", null); > 대신에 핸들러에 던진다
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다");
		}

		// 유효성 검사
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}
		
		// 권한
		if ((principal.getId() !=id)) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다.");
		}
		
		//핵심로직
		principal.setEmail(dto.getEmail());
		session.setAttribute("principal", principal);

		userRepository.save(principal);
		
		return new CMRespDto<>(1, "업데이트 성공", null);
	}

	@GetMapping("/user/{id}")
	public String userInfo(@PathVariable int id) {
		// 기본은 userRepository.findById(id) 디비에서 가져와야함.
		// 모델에 id를 담아가야함 = JSP로 치면 리퀘스트스코프.
		// 편법은 세션값을 가져올수도 있다.
		return "user/updateForm"; // 안에 Form태그가 있을꺼라서
	}

	@GetMapping("/logout")
	public String logout() {
		session.setAttribute("principal", null); // 아래와 동일하나 다른 키값이 안나갈수도있음.
		session.invalidate(); // 세션무효화(jsessionId에 있는 값을 비우는 것), 모든 서랍
		return "redirect:/";

		// return "board/list"; //게시글목록화면에 데이터가 있을까? (모델에서데이터안들고감)
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}

	@PostMapping("/login")
	public String login(@Valid LoginReqDto dto, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메시지 : " + error.getDefaultMessage());
			}
			model.addAttribute("errorMap", errorMap);
			return "error/error";
		}

		// 1. username, password 받기
		System.out.println(dto.getUsername());
		System.out.println(dto.getPassword());
		// 2. DB -> 조회

		String encPassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		User userEntity = userRepository.mLogin(dto.getUsername(), encPassword);

		if (userEntity == null) {
			return "redirect:/loginForm";
		} else {
			// 세션이 날라가는 조건 : 1.session.invaildate(), 2. 브라우저닫기를 닫으면날라감.
			// principal가 들고있는 값이 잘못들어오거나 null
			session.setAttribute("principal", userEntity);
			return "redirect:/";
		}
	}

	@PostMapping("/join")
	public @ResponseBody String join(@Valid JoinReqDto dto, BindingResult bindingResult, Model model) { // username=love&password=1234&email=love@nate.com

		// 1. 유효성 검사 실패 - 자바스크립트 응답(경고창, 뒤로가기)
		// 2. 정상 - 로그인 페이지

		// System.out.println("에러사이즈 : "+bindingResult.getFieldErrors().size());

		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메시지 : " + error.getDefaultMessage());
			}
			model.addAttribute("errorMap", errorMap);
			return Script.back(errorMap.toString());
		}

		// 프로그램은 누구나싫수할수있다. 실수하지 않게 만들어줘야한다.
		String encPassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassword);

		userRepository.save(dto.toEntity());
		return Script.href("/loginForm"); // 리다이렉션 (300)
	}

}
