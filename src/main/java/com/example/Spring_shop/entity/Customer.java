package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity //db에 저장
@Table(name="customer") //테이블명 item
@Getter
@Setter
@ToString
public class Customer extends BaseEntity{
    @Id  //PK
    @Column(name="customer_id") //칼럼 이름
    @GeneratedValue(strategy = GenerationType.AUTO) //PK auto로 증가
    private Long id; // 게시글 넘버

    @Column(name="subject" ,nullable = false,length = 50) //not null 길이 50
    private String subject; //제목

    @Column(name="userId", nullable = false)
    private String userId;

    @Lob
    @Column(name="content",nullable = false) //칼럼이름 price not null
    private String content; //내용

    @OneToOne(fetch = FetchType.LAZY) // 1:1 맵핑
    @JoinColumn(name = "member_id")
    private Member member;

}

