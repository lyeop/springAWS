package com.example.Spring_shop.service;

import com.example.Spring_shop.constant.PaymentStatus;
import com.example.Spring_shop.dto.OrderDto;
import com.example.Spring_shop.dto.OrderHistDto;
import com.example.Spring_shop.dto.OrderItemDto;
import com.example.Spring_shop.entity.*;
import com.example.Spring_shop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final PaymentRepository paymentRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);
        Payment payment = new Payment();

        Order order = Order.createOrder(member, orderItemList, payment);
        order.setOrderPrice(order.getTotalPrice());
        payment.setPrice(order.getTotalPrice());
        payment.setPaymentUid(order.getOrderUid());
        paymentRepository.save(payment);
        orderRepository.save(order);

        payment.setItemNm(order.getItemNmList());
        order.setItemNm(order.getItemNmList());

        return order.getId();
    }
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrder(email,pageable);
        Long totalCount =orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        //Order -> OrderHisDto
        //OrderItem -> OrderItemDto
        for (Order order : orders){// 주문리스트 -> 주문
            // 주문 히스토리 객체 생성시 매개변수 -> 주문 // 객체 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            // 주문에 있는 주문 아이템 리스트를 추출
            List<OrderItem> orderItems =order.getOrderItems();
            //주문 아이템 리스트에서 -> 주문 아이템
            for(OrderItem orderItem : orderItems){
                // 주문 아이템에 있는 item 을 추출하고 item id 를 매개변수로 입력
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(
                        // itemImg 객체를 추출 -> 조건 "Y" 대표 이미지 ItemImg
                        orderItem.getItem().getId(),"Y");
                // OrderItemDto 객체를 생성 -> 매개변수 orderItem 객체, itemImg -> url
                OrderItemDto orderItemDto = new OrderItemDto(orderItem,itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount);
    }
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Long orders(List<OrderDto> orderDtoList, String email){
        // Member 엔티티 객체 추출
        Member member = memberRepository.findByEmail(email);
        // 주문 Item 리스트 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 주문 Dto List 에 있는 객체만큼 반복
        for (OrderDto orderDto : orderDtoList){
            // 주문 -> Item 엔티티 객체 추출
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            System.out.println("99999999999999999999"+item.getBidPrice());
            System.out.println("아이템 이름"+ item.getItemNm());
            // 주문 Item 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            System.out.println(orderDto);
            // 주문 Item List 에 추가
            orderItemList.add(orderItem);
        }
        Payment payment = new Payment();

        Order order = Order.createOrder(member, orderItemList, payment);
        order.setOrderPrice(order.getTotalPrice());
        System.out.println(order.getOrderPrice()+"777777777777777777777777777");
        payment.setPaymentUid(order.getOrderUid());
        payment.setPrice(order.getTotalPrice());
        payment.setStatus(PaymentStatus.OK);
        paymentRepository.save(payment);
        orderRepository.save(order);

        payment.setItemNm(order.getItemNmList());
        order.setItemNm(order.getItemNmList());

        return order.getId();

        /////////////////// 주문 Item List 가 완성 ////////////////////////

        // 주문 Item List, Member 를 매개변수로 넣고
        // 주문서 생성
        // 주문서 저장

    }
}
