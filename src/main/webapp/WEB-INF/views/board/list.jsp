<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp"%>



<div class=container>

	<!-- var은 pageScope에 저장 -->
	<c:forEach var = 'board' items ='${boardsEntity.content}'>
		<!-- 카드글 시작 -->
		<div class="card">
			<div class="card-body">
				<!--el표현식은 변수명을 적으면 자동으로 get함수를 호출해준다 -->
				<h4 class="card-title">${board.title }</h4>
				<a href="/board/${board.id}" class="btn btn-primary">상세보기</a>
			</div>
		</div>
		<br />
		<!-- 카드글 끝 -->
	</c:forEach>

	
	<div class="d-flex justify-content-center">
		<ul class="pagination">
		<c:choose>
			<c:when test="${boardsEntity.first }">
				<li class="page-item disabled"><a class="page-link" href="/board?page=${boardsEntity.number-1}">Prev</a></li>
				<li class="page-item"><a class="page-link" href="/board?page=${boardsEntity.number+1 }">Next</a></li>
			</c:when>
			<c:when test="${boardsEntity.last }">
				<li class="page-item"><a class="page-link" href="/board?page=${boardsEntity.number-1}">Prev</a></li>
				<li class="page-item disabled"><a class="page-link" href="/board?page=${boardsEntity.number+1 }">Next</a></li>
			</c:when>

			<c:otherwise>
				<li class="page-item"><a class="page-link" href="/board?page=${boardsEntity.number-1}">Prev</a></li>
				<li class="page-item"><a class="page-link" href="/board?page=${param.page+1 }">Next</a></li>
			</c:otherwise>
		</c:choose>

		</ul>
	</div>

</div>



<%@ include file="../layout/footer.jsp"%>


