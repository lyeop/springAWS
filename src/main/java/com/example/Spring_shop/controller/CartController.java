package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.repository.ItemRepository;
import com.example.Spring_shop.service.CartService;
import com.example.Spring_shop.service.ItemService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final HttpSession httpSession;
    private final ItemService itemService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            //append를 이요해서 에러메세지가 없을때까지 붙이기
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        //유효성 검사를 하고 그게 걸리는게 있으면 싹다 엮어서 에이작스를 통해 보냄
        //StringBuilder 에러메이지 있는 String

        String email = getEmailFromPrincipalOrSession(principal);
        //메소드를 통해 소셜로그인인지, 일반로그인지 확인
        Long cartItemId;
        try {
            cartItemId = cartService.addCart(cartItemDto, email);
            //위에 유효성검사가 다 통과 될때 로그인한 계정에 아이템정보를 장바구니에 넣음
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        //장바구니아이디를 ajax로 보냄
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){

        String email = getEmailFromPrincipalOrSession(principal);
        //메소드를 이용해 소셜로그인지 일반로그인인지 확인
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(email);
        //이메일을 이용해서 카트의 내용을 조회하고 그걸 List로 만들어서 model로 정보 보냄
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       @RequestParam("bidPrice")int bidPrice, Principal principal) {
        if (bidPrice <= 0) {
            return new ResponseEntity<>("정수를 입력해주세요", HttpStatus.BAD_REQUEST);
        }
        String email = getEmailFromPrincipalOrSession(principal);
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemBidPrice(cartItemId, bidPrice);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }
    @PatchMapping(value = "/bid/cart/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartBidPrice(
            @PathVariable("cartItemId") Long cartItemId,
            @RequestBody BidRequest bidRequest, // 이 부분에서 'bidPrice'라는 이름으로 매개변수를 받습니다.
            Principal principal) {

        //ajax로 카트아이템 받고 , 리퀘스트바디로 클래스로 최고입찰가, 나의입찰가 , 아이템아이디 가져옵니다.
        String email = getEmailFromPrincipalOrSession(principal);
        //메소드이용해서 소셜로그인 , 일반로그인인지 확인하고
        if (!cartService.validateCartItem(cartItemId,email)){
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        //입찰을 했을때
        cartService.updateCartItemlowestBidPrice(cartItemId, bidRequest.getNewBidPrice()); //입찰가가 높으면 입력한 입찰가를 최고입찰가로 변경합니다.
        cartService.updatePriceItem(cartItemId,bidRequest.getLowestBidPrice()); // 현제 입찰가 나의입찰가
        cartService.updateCartItemBidPrice(cartItemId, bidRequest.getLowestBidPrice()); //원래 있던 입찰가를 최근입찰가로 변경합니다.
        //카트아이템 업데이트
        itemService.updateItemHighestBIdPrice(bidRequest.getItemId(), bidRequest.getLowestBidPrice()); // 현제 입찰가
        //아이템아이디가져온거로 아이템 입찰한 가격을 최근입찰가로 바꿉니다
        itemService.updateItemLowestBidPrice(bidRequest.getItemId(),bidRequest.getNewBidPrice()); //이전 입찰가
        //아이템아이디가져온거로 아이템의 최근입찰가 가격을 최고입찰가로 바굽니다
        //아이템 업데이트
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        //ajax로 보냅니다.
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal) {
        String email = getEmailFromPrincipalOrSession(principal);
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){


        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity<>("주문할 상품을 선택해주세요.", HttpStatus.FORBIDDEN);
        }


        String email = getEmailFromPrincipalOrSession(principal);
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), email)) {
                return new ResponseEntity<>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        Long orderId;
        try {
            orderId = cartService.orderCartItem(cartOrderDtoList, email);
            System.out.println("오더아이디~!~!~!~!~!~!~!~!~!~!~!~!~!~!~!~" + orderId);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
    //소셜 로그인인지 일반로그인지 구별 하는 메소드 세션을 통해 세션의 가져온 세션이름이 member면 email을 가져와서 소셜로그인의 이메일을 리턴
    //일반 로그인이면 이름을가져와서 이메일을 리턴
}