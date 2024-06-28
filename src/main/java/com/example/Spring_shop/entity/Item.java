package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.ItemSellStatus;
import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.dto.ItemFormDto;
import com.example.Spring_shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity //db에 저장
@Table(name="item1") //테이블명 item
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id  //PK
    @Column(name="item_id") //칼럼 이름
    @GeneratedValue(strategy = GenerationType.AUTO) //PK auto로 증가
    private Long id; // 상품코드

    @Column(nullable = false,length = 50) //not null 길이 50
    private String itemNm; //상품명

    @Column(name="Price",nullable = false) //칼럼이름 price not null
    private int price; //가격

    @Column(nullable = false) //not null
    private int stockNumber; //수량

    @Lob  //대용량 저장
    @Column(nullable = false) // not null
    private String itemDetail; //상품상세설명

    @Enumerated(EnumType.STRING) // Enum을 String 으로 변환해서 저장
    private ItemSellStatus itemSellStatus; //상품판매 상태

    @Enumerated(EnumType.STRING)
    private ItemValue itemValue;

//    private LocalDateTime regTime; //등록시간

//    private LocalDateTime updateTime; //수정시간

    @ManyToMany(fetch = FetchType.LAZY) //다대 다 기능
    @JoinTable(name = "member_item", joinColumns =@JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Member> members;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemValue= itemFormDto.getItemValue();
    }
    public void removesStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber; //10 ,5 /10 ,20
        if (restStock<0){
            throw  new OutOfStockException
                    ("상품재고가 부족합니다.(현재 재고수량:"+this.stockNumber+")");
        }
        this.stockNumber = restStock;
    }
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
