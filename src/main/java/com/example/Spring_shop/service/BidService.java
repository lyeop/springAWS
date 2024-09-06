package com.example.Spring_shop.service;

import com.example.Spring_shop.dto.BidRequest;

import com.example.Spring_shop.entity.Item;

import com.example.Spring_shop.repository.ItemRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class BidService {

    private final ItemRepository itemRepository;


    public int getCurrentBidPrice(Long itemId) {
        // 여기서는 데이터베이스나 외부 시스템에서 최신 입찰가를 가져와 반환하는 로직을 작성합니다.
        // 예를 들어, 데이터베이스에서 최신 입찰가를 조회하는 코드를 작성할 수 있습니다.
        // 임시로 15000을 반환하도록 작성하겠습니다.
        return getHighestBidPrice(itemId); // 실제로는 데이터베이스에서 조회한 값을 반환해야 합니다.
        //itemId를 가지고 아이템의 최입찰가 조회
    }
    public int getLowcurrendBidprice(Long itemId){
        if(getSLowestBidPriceByItemId(itemId)==0){
            return getStartingBidPrice(itemId);
            //만약에 최근 입찰가가 0 이면 경매시작가로 리턴으로 보냄
        }
        else{
            return getSLowestBidPriceByItemId(itemId);
            //itemId 를 가지고 최근 입찰가 조회
        }

    }

    public int placeBid(Long itemId,BidRequest bidRequest, String username) {
        // 입찰 처리 로직
        // 예시로 새로운 입찰을 저장하는 코드를 작성합니다.
        System.out.println(bidRequest.getItemId());
        System.out.println("입찰"+bidRequest.getBidPrice());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        if(bidRequest.getBidPrice()>item.getBidPrice()) {
            item.setBidPrice(bidRequest.getBidPrice());
            item.updateBidPrice(bidRequest.getBidPrice());
        }
        //최고입찰가랑 현재 입찰가를 비교하고 일찰가가 최고입찰가 보다 높을시 최괴입찰가 업데이트

        // 해당 아이템의 최고 입찰가를 반환하도록 수정합니다.
        return getHighestBidPrice(bidRequest.getItemId());
        //최고입찰가 조회한 ItemId 가지고 업데이트
    }
    public int lowPlaceBid(Long itemId,BidRequest bidRequest, String username) {
        // 시작 입찰가
        // 예시로 새로운 입찰을 저장하는 코드를 작성합니다.
        System.out.println(bidRequest.getItemId());

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        item.setLowestBidPrice(bidRequest.getLowestBidPrice());
        item.updateLowestBidPrice(bidRequest.getLowestBidPrice());
        //최고입찰가가 입찰금액보다 낮아지면 최근입찰금액을 최고입찰가로 변경

        // 해당 아이템의 최고 입찰가를 반환하도록 수정합니다.
        return getSLowestBidPriceByItemId(bidRequest.getItemId());
        //최고입찰가 -> 최근입찰가로 아아템아이디를 가지고 업데이트
    }

    // 특정 아이템의 최고 입찰가를 조회하는 메서드
    public int getHighestBidPrice(Long itemId) {
        Integer bidPrice = itemRepository.findBidPriceByItemId(itemId);
        return (bidPrice != null) ? bidPrice : 0;
        //아아템아이디를 가지고 레포지토리로 아이템 최고입찰가를 조회하고 만약 null이면 0으로 치환
    }
    public int getSLowestBidPriceByItemId(Long itemId) {
        Integer LowestBidPrice=  itemRepository.findLowestBidPriceByItemId(itemId);
        return (LowestBidPrice != null) ? LowestBidPrice : 0;
        //아이템 아이디를 가지고 레포지토리 아이템 최근입찰가 조회하고 만약 null이면 0으로 최환
    }


    public void updateItemLowestBidPrice(Long itemId, int bidPrice){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        if(item.getBidPrice()>bidPrice){
            item.setPrice(item.getBidPrice());
        }else{
            item.setBidPrice(bidPrice);
            item.updateBidPrice(bidPrice);
        }
        //입찰가 비교하고 아이템 최고입찰가 업데이트
    }

    public int getStartingBidPrice(Long itemId) {
        Integer StartingBidPrice=  itemRepository.findStartingBidPriceByItemId(itemId);
        return (StartingBidPrice != null) ? StartingBidPrice : 0;
    }
    //아이템아이디로 경매시작가 조회 -> null 이면 0으로 치환
    public String findItemNm(Long itemId){
        String findItemNm = itemRepository.findByItemNmItemId(itemId);
        return (findItemNm !=null) ? findItemNm : "Non";
        //아이템 아이디로 아이템 이름 조회 -> 널 이면 "Non" 으로 치환
    }

}
