package com.cos.blogapp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotfoundException;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	final private BoardRepository boardRepository;
	final private CommentRepository commentRepository;

	// 트랜젝션 어노테이션 (트랜젝션을 시작하는 것)
	// rollbackFor (함수내부에 하나의 write라도 실패하면 전체를 rollback 하는 것)
	// 주의 : Runtime
	@Transactional(rollbackFor = MyNotfoundException.class)
	public void 댓글등록(int boardId, CommentSaveReqDto dto, User principal) {
		// 로직
		// 1. DTO로 데이터 받기
		// 2. 코멘트 객체 만들기 - 빈객체
		Board boardEntity = boardRepository.findById(boardId)
				.orElseThrow(() -> new MyNotfoundException(boardId + "를 찾을 수 없습니다."));

		// 3. 코멘트 객체 값 추가하기, id:X, content:DTO값,user:세션값, boardID로 findById
		// 인설트 목적은 보드객체!!
		Comment comment = new Comment();
		comment.setContent(dto.getContent());
		comment.setUser(principal);
		comment.setBoard(boardEntity);

		// 4. Save하기
		commentRepository.save(comment);
		// 이 전체가 트렌젹션 - 서비스
	}

	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 게시글수정(int id, User principal, BoardSaveReqDto dto) {
		// 권한
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyAsyncNotFoundException(id + "를 찾을 수 없습니다."));
		if ((principal.getId() != boardEntity.getUser().getId())) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다.");
		}

		// 핵심기능
		// User principal = (User) session.getAttribute("principal");
		Board board = dto.toEntity(principal);
		board.setId(id);// update의 핵심

		boardRepository.save(board);
	}

	public Board 게시글수정페이지이동(int id) {
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotfoundException(id + "번호의 게시글을 찾을수 없습니다."));

		return boardEntity;
	}

	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 게시글삭제(int id, User principal) {
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotfoundException(id + "를 찾을 수 없습니다."));
		if ((principal.getId() != boardEntity.getUser().getId())) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다.");
		}

		try {
			boardRepository.deleteById(id); // (id가 없으면) 에러 발생

		} catch (Exception e) {
			throw new MyAsyncNotFoundException(id + "아이디를 찾을수 없어 실행할수 없습니다.");
		}
	}

	public Board 게시글상세보기(int id) {
		// 1. orElse는 값을 찾으면 Board가 리턴, 못찾으면(괄호안 내용)
//		Board boardEntity = boardRepository.findById(id)
//				.orElse(new Board(100, "글X", "긅",null));

		// 2. orElseThrow
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotfoundException(id + "를 찾을 수 없습니다."));
		// 실제로 터진 익셥센은 다른건데 이게 터졌다고 보냄 > 익셉션고정

		return boardEntity;
	}

	@Transactional(rollbackFor = MyNotfoundException.class)
	public void 게시글등록(BoardSaveReqDto dto, User principal) {

		// <p> 날리기
		dto.setContent(dto.getContent().replace("<p>", ""));
		dto.setContent(dto.getContent().replace("</p>", ""));

		boardRepository.save(dto.toEntity(principal)); // 데이터베이스 인설트

	}

	public Page<Board> 게시글목록보기(int page) {

		PageRequest pageRequest = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest);

		return boardsEntity;
	}
}
