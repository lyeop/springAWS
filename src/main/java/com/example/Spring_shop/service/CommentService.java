package com.example.Spring_shop.service;

import com.example.Spring_shop.constant.Role;
import com.example.Spring_shop.dto.AddCommentRequest;

import com.example.Spring_shop.entity.Comment;
import com.example.Spring_shop.entity.CustomerCenterPost;
import com.example.Spring_shop.entity.Member;

import com.example.Spring_shop.repository.CommentRepository;
import com.example.Spring_shop.repository.CustomerCenterRepository;
import com.example.Spring_shop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {

    @Autowired
   private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CustomerCenterRepository postRepository;

    public Comment addComment(AddCommentRequest request) {
        // 유저와 포스트 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        CustomerCenterPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .comment(request.getComment())
                .maskedName(maskName(member.getName()))
                .isAdmin(member.getRole().equals(Role.ADMIN))
                .build();

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Comment: " + comment);

        return commentRepository.save(comment);
    }
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }
    public void deleteComment(Long id){

        commentRepository.deleteById(id);
    }
    private String maskName(String name) {
        if (name.length() > 2) {
            return name.substring(0, name.length() - 2) + "**"; // 끝 두 글자 마스킹
        }
        return name;
    }
}
