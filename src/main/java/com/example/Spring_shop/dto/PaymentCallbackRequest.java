package com.example.Spring_shop.dto;

import lombok.Data;

@Data

public class PaymentCallbackRequest {
    private String paymentUid; // 결제 고유 번호
    private String orderUid; // 주문 고유 번호

}
