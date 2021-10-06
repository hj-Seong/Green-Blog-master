package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotfoundException;
import com.cos.blogapp.service.BoardService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙은 필드에 대한 생성자가 만들어진다.
@Controller // 컴포넌트 스캔(스프링) IoC
public class BoardController {

	// IOC에 있는것 가져오기 DI
	private final BoardService boardService;
	private final HttpSession session;

	@PostMapping("/board/{boardId}/comment") // 최종목적지!! 두가지목적지가 섞여있다.
	public String commentSave(@PathVariable int boardId, CommentSaveReqDto dto) {

		User principal = (User) session.getAttribute("principal");	
		//인증
		 if ((principal == null)) { 
			 //return new CMRespDTO<>(-1, "", null); > 대신에 핸들러에던진다 
			 throw new MyNotfoundException("인증이 되지 않았습니다"); }
		 
		boardService.댓글등록(boardId, dto, principal);

		return "redirect:/board/" + boardId;
	}

	@PutMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody BoardSaveReqDto dto,
			BindingResult bindingResult) {// <?> 묵시적타입추론, return때!

		// 공통로직 > 안전, 나중에 프레임워크의 기능을 사용해서 처리가능.
		// 유효성 검사
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}

		// 인증
		User principal = (User) session.getAttribute("principal");
		if ((principal == null)) {
			// return new CMRespDTO<>(-1, "", null); > 대신에 핸들러에 던진다
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다");
		}
		
		boardService.게시글수정(id, principal, dto);
		
		return new CMRespDto<String>(1, "업데이트 성공", null);
	}

	@GetMapping("/board/{id}/updateForm") // 모덜에 접근할 때 마다 주소고정,아니면 마음대로OK
	public String boardUpdateForm(@PathVariable int id, Model model) { // 클릭한 보드id
		// 인증 권한 > 이때는 상관없다,수정하기 버튼을 눌렀을때가 중요!(서버쪽 요청)
		// 게시글 정보를 가지고 가야함. 그냥이동 : 데이터X. 데이터 들고가면 주소에!
		
		//규칙을 맞춘것
		model.addAttribute("boardEntity",  boardService.게시글수정페이지이동(id));
		
		return "board/updateForm";
	}

	// API(AJAX)
	@DeleteMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> deleteById(@PathVariable int id) {

		// 인증이 된 사람만 함수 접근 가능!! (로그인 된 사람)
		// 권한이 있는 사람만 함수 접근 가능(principal.id == {id})

		User principal = (User) session.getAttribute("principal");
		if ((principal == null)) {
			throw new MyAsyncNotFoundException("인증이되지 않았습니다.");
		}

		boardService.게시글삭제(id, principal);

		return new CMRespDto<String>(1, "성공", null);
	}

	// 주소 만들어줌 >> 스프링에서는 패스밸루어블이 좋다.
	// 쿼리스트링, 패스var =>디비 where에 걸리는 친구들
	// 1. 컨트롤러 선정 2. HTTP Method 성정 3. 받을 데이터가 있는지!!(body / 쿼리스트링, 패스var)
	// 4. 디비에 접근을 해야하면 Model 접근하기 orElse Model에 접근할 필요가 없다.
	@GetMapping("/board/{id}")
	public String detail(@PathVariable int id, Model model) {
		// select * from board where id = :id *프라이머리키
		// 지원안해주면 직접적어야한다.

		//Board 객체에 존재하는 것(Board(o), User(o), List<Comment>(x) )
		model.addAttribute("boardEntity", boardService.게시글상세보기(id));
		return "board/detail"; //ViewResolver

	}

	@PostMapping("/board")
	// String title, String content 벨리데이션 체크가 되지않으므로 DTO를 만들어서 사용
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		// String만하면 파일, ResponseBody하면, 글로

		// 공통로직 시작--------------------------------------
		// >>핵심기능이아니라, 공통기능(AOP 관점지향이 좋다, 뺴서 정리가능)
		// 인증 체크
		User principal = (User) session.getAttribute("principal");
		// 다운캐스트, 형변환 User는 User 타입이면서 Object 타입.
		if (principal == null) {
			return Script.href("/loginForm", "잘못된접근입니다");
		}
		// 안해주면 데이터베이스가 꼬인다.

		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}

		//공통로직 끝 ----------------------------------------
		
//		User user = new User();
//		user.setId(3);
//		boardRepository.save(dto.toEntity(user));

		
		// 핵심로직 시작 ------------------
		boardService.게시글등록(dto, principal);
		// 핵심 로직 끝 --------------------------
		
		return Script.href("/", "글쓰기 성공");
//		return "redirect:/"; //데이터베이스에서 값을들고오는것 모델,엔티티 (mvc패턴에서)
	}

	// board 모델을 가져온다, 데이터베이스와 상관없는거 주소마음대로.
	@GetMapping("/board/saveForm")
	public String saveForm() { // 함수이름은 주소로
		return "board/saveForm"; // yml 서피스 - 뷰리졸버.
	}

	// board?page=2 >> 구체적인 요정
	// DB로 주소설계규칙
	@GetMapping({ "/board" })
	public String home(Model model, int page) {

		// 데이터에서 받을때에 이름의 규칙
		model.addAttribute("boardsEntity",boardService.게시글목록보기(page));
		// 연속화된 오브젝트는 그 오브젝트를 당길떄 오브젝트한다.

		return "board/list";
	}

}
