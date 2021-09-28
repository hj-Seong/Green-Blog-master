package com.cos.blogapp.web.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data //getter, setter.
public class BoardSaveReqDto {
	@Size(min = 1, max = 500)
	@NotBlank
	private String title; // 아이디

	private String content;
	
	public Board toEntity( User principal) {
		Board board = new Board();
		
		//board.setId(); //PK (자동증가 번호)
		board.setTitle(title); // 아이디
		board.setContent(content);
		board.setUser(principal);
		return board;
	}

}
