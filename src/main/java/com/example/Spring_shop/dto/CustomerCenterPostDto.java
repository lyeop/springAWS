package com.example.Spring_shop.dto;


import com.example.Spring_shop.constant.Notice;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCenterPostDto {

    private Long id;
    private String writer;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private Notice notice;

    // 생성자, getter, setter
    @QueryProjection
    public CustomerCenterPostDto(Long id, String writer, String title, String content) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
    }
}