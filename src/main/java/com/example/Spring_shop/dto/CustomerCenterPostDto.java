package com.example.Spring_shop.dto;


import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.entity.CustomerCenterPost;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCenterPostDto {

    private String memberEmail;
    private Long id;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private Notice notice;
    private int views;

    // 생성자, getter, setter
    @QueryProjection
    public CustomerCenterPostDto(Long id, String title, String content, int views) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.views = views;
    }

    public CustomerCenterPostDto(CustomerCenterPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.memberEmail = post.getMember().getEmail();
        this.notice = post.getNotice();
        this.views = post.getViews();
    }
}