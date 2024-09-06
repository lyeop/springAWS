package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.BidRequest;
import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.dto.BidData;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.repository.ItemRepository;
import com.example.Spring_shop.service.BidService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService; // 비즈니스 로직을 처리할 서비스 클래스

    private final HttpSession httpSession;
    private final ItemRepository itemRepository;


    @GetMapping(value = {"/fetchBidData/{itemId}","/cart/fetchBidData","/item/**"})
    public BidData fetchBidData(@PathVariable("itemId")Long itemId) {
        int currentBidPrice = bidService.getCurrentBidPrice(itemId); //최고가 조회
        int lowBidPirce = bidService.getLowcurrendBidprice(itemId); //최저가 조히
        return new BidData(currentBidPrice,lowBidPirce);
        //단일 상품 그래프
    }

    @PatchMapping(value = {"/bid/{itemId}"})
    public @ResponseBody ResponseEntity<?> placeBid(@PathVariable("itemId") Long itemId, @RequestBody BidRequest bidRequest, Principal principal) {
        // Check if the itemId in the path matches the itemId in the body
        if (!itemId.equals(bidRequest.getItemId())) {
            return ResponseEntity.badRequest().body("Item ID in the path and body do not match.");
        }
        String email = getEmailFromPrincipalOrSession(principal);

        int newBidPrice = bidService.placeBid(itemId,bidRequest, email);
        System.out.println("세션아이디" + principal.getName());
        int currentBidPrice = bidService.lowPlaceBid(itemId,bidRequest, email);


        System.out.println("입찰"+newBidPrice);
        System.out.println("최고금액"+currentBidPrice);

        if (bidRequest.getBidPrice() <= currentBidPrice) {
            System.out.println("입찰"+newBidPrice);
            System.out.println("최고금액"+currentBidPrice);
            return ResponseEntity.badRequest().body("최고 입찰가 보다 입찰 가격이 낮습니다.");
        }else {
            bidService.updateItemLowestBidPrice(itemId, newBidPrice);

            System.out.println("아이템 아이디: " + itemId);
            System.out.println(newBidPrice);

            // bidService.updateItemBidPrice(itemId, newBidPrice);

            Map<String, Object> response = new HashMap<>();
            response.put("newBidPrice", newBidPrice);

            return ResponseEntity.ok(response);
        }
    }
    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
    // 여러 상품의 최고 입찰가를 가져오는 메소드
    @GetMapping("/fetchBidBigData/{itemId}")
    public ResponseEntity<BidData> fetchBidBigData(@PathVariable("itemId") Long itemId) {
        List<BidData> bidDataList = new ArrayList<>();

        int currentBidPrice = bidService.getCurrentBidPrice(itemId);
        int lowBidPrice = bidService.getLowcurrendBidprice(itemId);
        String itemNm = bidService.findItemNm(itemId);
        BidData bidData = new BidData(currentBidPrice, lowBidPrice);
        bidData.setItemNm(itemNm);

        return ResponseEntity.ok(bidData);
    }
    //장바구니 그래프 > 다수 > 최고입찰가, 최근입찰가 , 상품이름 itemId 조회하고 List<BidData> 리스트로 만들어서
    //ajax 값 조회해서 각자상품 업데이트

    private boolean isUserLoggedIn() {
        // 로그인 상태를 확인하는 로직
        return true; // 예시로 true 반환, 실제로는 세션이나 인증 토큰을 확인
    }

}
