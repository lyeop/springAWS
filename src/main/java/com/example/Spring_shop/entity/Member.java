package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity // 나 엔티티야
@Table(name = "member") // 테이블 명
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    //기본키 컬럼명 = member_id AI-> 데이터 저장시 1씩 증가
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //알아서 설정
    private String name;
    // 중복 허용 X
    @Column(unique = true)
    private String email;
    //알아서
    private String password;
    //알아서
    private String address;

    private String tel;



    //Enum -> 컴 : 숫자 우리 : 문자
    //데이터베이스 문자 그대로 -> USER, ADMIN
    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember1(User user){
        Member member = new Member();
        member.setName(user.getName());
        member.setEmail(user.getEmail());
        member.setRole(Role.USER);
        return member;
    }
}