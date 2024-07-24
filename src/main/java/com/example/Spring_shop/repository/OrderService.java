package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.entity.Order;

public interface OrderService {
    Order autoOrder(Member member);

}
