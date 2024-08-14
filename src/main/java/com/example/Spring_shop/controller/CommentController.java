package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.AddCommentRequest;
import com.example.Spring_shop.entity.Comment;
import com.example.Spring_shop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody AddCommentRequest request) {
        try {
            Comment comment = commentService.addComment(request);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            // 로그에 예외를 기록합니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 추가 중 오류 발생");
        }
    }
}