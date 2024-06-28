package com.example.Spring_shop.dto;

import com.example.Spring_shop.constant.ItemSellStatus;
import com.example.Spring_shop.constant.ItemValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    private String searchDateType; //조회 날짜

    private ItemSellStatus searchSellStatus; //상태

    private ItemValue itemValue; //상품 종류

    private String searchBy; //조회 유형

    private String searchQuery = ""; //검색 단어
}
