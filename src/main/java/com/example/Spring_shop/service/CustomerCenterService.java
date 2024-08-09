package com.example.Spring_shop.service;


import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.CustomerCenterPost;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.CustomerCenterCustom;
import com.example.Spring_shop.repository.CustomerCenterRepository;
import com.example.Spring_shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCenterService {

    private final CustomerCenterRepository customerCenterRepository; //자동 주입됨
    private final MemberRepository memberRepository;
    private final CustomerCenterCustom customerCenterCustom;

    public void CreateCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto,
                                         String email) {

        // 사용자의 이메일을 기준으로 Member 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        // 게시글 생성
        CustomerCenterPost post = new CustomerCenterPost();
        post.setTitle(customerCenterPostFormDto.getTitle());
        post.setContent(customerCenterPostFormDto.getContent());
        post.setNotice(customerCenterPostFormDto.getNotice());
        post.setMember(member);

        customerCenterRepository.save(post);
    }
    @Transactional(readOnly = true)
    public Page<CustomerCenterPostDto> getCustomerPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return customerCenterCustom.getAllpage(itemSearchDto, pageable);
    }



    // 게시물 ID로 조회
    public CustomerCenterPost getCustomerCenterPostById(Long id) {
        CustomerCenterPost post = customerCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 조회수 증가
        post.setViews(post.getViews()+1);
        // 업데이트된 게시글 저장
        customerCenterRepository.save(post);

        return post;
    }
    // 게시물 삭제 메소드
    public void deleteCustomerCenterPostById(Long id) {
        customerCenterRepository.deleteById(id);
    }

    // 게시물 업데이트 메소드
    public Long updateCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto
    ){

        CustomerCenterPost customerCenterPost = customerCenterRepository.findById(customerCenterPostFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        customerCenterPost.updateCustomerCenterPost(customerCenterPostFormDto);

        return customerCenterPost.getId();
    }

    @Transactional(readOnly = true)
    public CustomerCenterPostFormDto getCustomerCenterPost(Long id){
        CustomerCenterPost customerCenterPost = customerCenterRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        CustomerCenterPostFormDto customerCenterPostFormDto = CustomerCenterPostFormDto.of(customerCenterPost);

        return customerCenterPostFormDto;


    }
    public Page<CustomerCenterPostDto> getMyPost(ItemSearchDto itemSearchDto, Pageable pageable) {
        return customerCenterCustom.getMyPage(itemSearchDto, pageable);
    }
}



