package com.cos.blogapp.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BoardControllerTest {
	
	private final BoardRepository boardRepository;
	
	@GetMapping("/test/board/{id}")
	public Board detail(@PathVariable int id) {
		//영속성컨텍스트 = Board(User 있음, List<Commnet> 없음 )
		return boardRepository.findById(id).get(); //MessagerConverter
	}
}
