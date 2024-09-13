package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.AddCommentRequest;
import com.example.Spring_shop.entity.Comment;
import com.example.Spring_shop.repository.CommentRepository;
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
            System.out.println(request.getComment()+"내용");
            System.out.println(request.getMemberId()+"맴버");
            System.out.println(request.getPostId()+"게시글");
            Comment comment = commentService.addComment(request);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            // 로그에 예외를 기록합니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 추가 중 오류 발생");
        }
    }
    @PostMapping("/delete/{commentId}")
    public @ResponseBody ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        System.out.println("제대로 댓글 아이디 들어오는지"+commentId);
        try {
            commentService.deleteComment(commentId); // 댓글 삭제 서비스 호출
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생");
        }
    }
}