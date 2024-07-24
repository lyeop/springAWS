package com.example.Spring_shop.repository;


import com.example.Spring_shop.dto.CustomerSearchDto;
import com.example.Spring_shop.entity.CustomerCenterPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerCenterRepository extends JpaRepository<CustomerCenterPost, Long> {
    CustomerCenterPost findByTitle(String title);
    Optional<CustomerCenterPost> findById(Long id);




}