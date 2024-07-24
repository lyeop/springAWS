package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/*
@Service
@Transactional
@RequiredArgsConstructor

public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member autoRegister() {
       // Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email("example@example.com")
                .address("서울특별시 서초구 역삼동")
                .build();

        return memberRepository.save(member);
    }

}

 */
