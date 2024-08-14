package com.example.Spring_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AddCommentRequest {

    private Long postId; // 게시글 ID
    private Long memberId; // 회원 ID
    private String comment; // 댓글 내용

    // 기본 생성자와 모든 필드를 받는 생성자 생성
    public AddCommentRequest(Long postId, Long memberId, String comment) {
        this.postId = postId;
        this.memberId = memberId;
        this.comment = comment;
    }
}
