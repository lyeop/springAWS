package com.example.Spring_shop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentProduct {

    private String imageUrl;  // 상품 이미지 URL
    private Long productId;   // 상품 ID
    private String itemNm;

    // 기본 생성자 (Jackson JSON 라이브러리에서 필요)
    public RecentProduct() {
    }

}
