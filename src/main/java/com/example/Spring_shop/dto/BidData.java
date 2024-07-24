package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidData {
    private String itemNm;
    private int bidPrice;
    private int lowesBidPrice;
    private int startingBidPrice;

    public BidData(int bidPrice,int startingBidPrice) {
        this.bidPrice = bidPrice;
        this.lowesBidPrice=startingBidPrice;
    }
    //그래프

}