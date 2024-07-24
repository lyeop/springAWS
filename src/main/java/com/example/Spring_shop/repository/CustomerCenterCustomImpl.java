package com.example.Spring_shop.repository;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.dto.CustomerCenterPostDto;
import com.example.Spring_shop.dto.CustomerSearchDto;


import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.dto.QCustomerCenterPostDto;
import com.example.Spring_shop.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CustomerCenterCustomImpl implements CustomerCenterCustom{

    private JPAQueryFactory queryFactory;
    public CustomerCenterCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em); // JPAQueryFactory 실질적인 객체가 만들어 집니다.
    }
    @Override
    public Page<CustomerCenterPostDto> getAllpage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QCustomerCenterPost customerCenterPost = QCustomerCenterPost.customerCenterPost;

        QueryResults<CustomerCenterPostDto> results = queryFactory.select(new QCustomerCenterPostDto(
                        customerCenterPost.id,
                        customerCenterPost.writer,
                        customerCenterPost.title,
                        customerCenterPost.content
                ))
                .from(customerCenterPost)
                .where(postNotice(itemSearchDto.getNotice()))
                .orderBy(customerCenterPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<CustomerCenterPostDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
    private BooleanExpression postNotice(Notice notice) {
        return notice == null ? null : QCustomerCenterPost.customerCenterPost.notice.eq(notice);
    }
}



