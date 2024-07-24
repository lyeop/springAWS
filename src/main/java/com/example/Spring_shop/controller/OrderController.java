package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.CartDetailDto;
import com.example.Spring_shop.dto.OrderDto;
import com.example.Spring_shop.dto.OrderHistDto;
import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.entity.Order;
import com.example.Spring_shop.service.MemberService;
import com.example.Spring_shop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final HttpSession httpSession;
    private final MemberService memberService;

    @PostMapping(value = "/order")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                         Principal principal){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessages(bindingResult), HttpStatus.BAD_REQUEST);
        }
        //유효성검사
        //ajax이용해서 OrderDto를 통해서 데이타를 받아오고 로인한사람이 누군지 체크
        String email = getEmailFromPrincipalOrSession(principal);
        email = processSocialLoginEmail(email);
        if (email == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            //유효성검사를 통과하면 Long 으로 변수를 만들어서 서비를 이용해서 받아온 객체를 구매이력에 넣어줍니다.
            Long orderId = orderService.order(orderDto, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        String email = getEmailFromPrincipalOrSession(principal);
        if (email == null) {
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }
        //구매이력 페이지에 들어가면 로그인한사람을 가지고 로그인한살삼이 가지고 있는 구매이력 데이터를 조회합니다.
        email = processSocialLoginEmail(email);
        System.out.println(email);
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(email, pageable);
        //가지고있는 이메일로 데이터를 조회해서 page형식으로 만들어서 list로 저장합니다
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        //만든 리스틀 보내줘서 출력하게 해줌
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        String email = getEmailFromPrincipalOrSession(principal);
        if (email == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!orderService.validateOrder(orderId, email)) {
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null && isSocialLogin(user)) {
            return user.getEmail();
        }
        return principal != null ? principal.getName() : null;
    }

    private String processSocialLoginEmail(String email) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null && isSocialLogin(user)) {
            String provider = user.getProvider();
            if ("google".equals(provider) || "naver".equals(provider) || "kakao".equals(provider)) {
                return user.getEmail();
            }
        }
        return email;
    }

    private boolean isSocialLogin(SessionUser user) {
        String provider = user.getProvider();
        return provider != null && ("google".equals(provider) || "naver".equals(provider) || "kakao".equals(provider));
    }

    private String getErrorMessages(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage());
        }
        return sb.toString();
    }
    //에러메세지 생기면 생긴거 다 이어서 append 엮어서 전송
    @GetMapping("/order")
    public String order(@RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "orderUid", required = false) String id,
                        Model model) {

        model.addAttribute("message", message);
        model.addAttribute("orderUid", id);

        return "order";
        //결제할때 필요한 메소드
    }


}
