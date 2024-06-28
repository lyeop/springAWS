package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId;
    private String itemNm;
    private int price;
    private int count;
    private String imgUrl;
    private LocalDateTime localDateTime;


    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl
    ,LocalDateTime localDateTime) {
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
        this.localDateTime=localDateTime;
    }


}
