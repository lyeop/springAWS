package com.example.Spring_shop.repository;

import com.example.Spring_shop.dto.CustomerCenterPostDto;
import com.example.Spring_shop.dto.CustomerSearchDto;
import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.entity.CustomerCenterPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerCenterCustom {
    Page<CustomerCenterPostDto> getAllpage(ItemSearchDto itemSearchDto, Pageable pageable);
}
