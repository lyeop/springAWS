package com.example.Spring_shop.dto;

import com.example.Spring_shop.entity.Member;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestPayDto {

    private String itemNm;
    private String buyerName;
    private int orderPrice;
    private String buyerEmail;
    private Member.Address buyerAddress;
    private String orderUid;

    @Builder
    public RequestPayDto(String itemNm, String buyerName, int orderPrice, String buyerEmail, Member.Address buyerAddress, String orderUid) {


        this.itemNm = itemNm;
        this.buyerName = buyerName;
        this.orderPrice = orderPrice;
        this.buyerEmail = buyerEmail;
        this.buyerAddress = buyerAddress;
        this.orderUid = orderUid;
    }
}