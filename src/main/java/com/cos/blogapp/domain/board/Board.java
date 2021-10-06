package com.cos.blogapp.domain.board;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; //PK (자동증가 번호)
	
	@Column(nullable = false ,length = 50)
	private String title; // 아이디
	@Lob
	private String content;
	
	@JoinColumn(name = "userId")
	@ManyToOne(fetch = FetchType.EAGER) //찾을때 select-lazy전략
	//어차피 당겨올것이 하나밖에없기에 전략이 EAGER
	//1:n일 경우 전부 lazy ex)댓글
	private User user;
	
	// 양방향 맵핑
	// mappedBy에는 FK의 주인의 변수이름을 추가한다.
	@JsonIgnoreProperties({"board"}) //comments 객체의 내부의 필드를 제외 시키는 법
	//파일 리턴때는 필요X 
	@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
	private List<Comment> comments;
	
}
