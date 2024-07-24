package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="orders")
@NoArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    @Column(name = "order_price")
    private int orderPrice;
    private String itemNm;
    private String orderUid; // 주문 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    //양방향 cascade 부모따라갑니다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;






//    private LocalDateTime regTime;

//    private LocalDateTime updateTime;

    //주문서 주문아이템 리스트에 주문 아이템 추가
    //주문 아이쳄에 주문서 추가
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    //주문서 생성
    //현재 로그인된 멤버 주문서에 추가
    //주문아이템 리스트를 반복문을 통해서 주문서에 추가
    //상태는 주문으로 세팅
    //주문 시간은 현재시간으로 세팅
    //주문서 리턴
    public static Order createOrder(Member member, List<OrderItem> orderItemList, Payment payment){
        Order order = new Order();
        order.setMember(member);
        order.setOrderUid(UUID.randomUUID().toString());
        order.setPayment(payment);
        System.out.println(orderItemList.size()+"====================================");
        for(OrderItem orderItem : orderItemList){

            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    @Builder
    public Order(int orderPrice, String itemNm, Long id, String orderUid, Member member, Payment payment) {
        this.orderPrice = orderPrice;
        this.itemNm = itemNm;
        this.orderUid = UUID.randomUUID().toString();
        this.member = member;
        this.payment = payment;
        this.id = id;
    }

    //주문서에 있는 주문 아이템 리스트를 반복
    //주문 아이템마다 총 가격을 totalPrice에 추가
    //결론 비싸다 -> 개발자 되야 한다. 괜찮다.(실력주의)
    public int getTotalPrice(){
        int totalPrice=0;
        for (OrderItem orderItem : orderItems){
            totalPrice +=orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public String getItemNmList(){
        StringBuilder getItemNm= new StringBuilder();
        for(OrderItem orderItem : orderItems){
            getItemNm.append(orderItem.getItemNm());
            getItemNm.append("<br>");
        }
        return getItemNm.toString();
    }

    public void cancelOrder(){
        this.orderStatus=OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
