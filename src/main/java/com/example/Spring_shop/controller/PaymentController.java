package com.example.Spring_shop.controller;


import com.example.Spring_shop.dto.PaymentCallbackRequest;
import com.example.Spring_shop.dto.RequestPayDto;
import com.example.Spring_shop.repository.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor

public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable(name = "id", required = false) Long id,
                              Model model) {
        System.out.println("들어오는지 확인~!~!~!~!~!~!~!~!~!");
        RequestPayDto requestPayDto = paymentService.findRequestDto(id);
        System.out.println(requestPayDto.getOrderPrice());
        System.out.println("상품이름"+requestPayDto.getItemNm());
        System.out.println("페이먼트 확인!@!@!@!@!@!@!@!@!!@!@!@!@!@!");
        model.addAttribute("requestDto", requestPayDto);
        //결제 할때 아이디 조회해서 id가지고 Dto객체를 받아서 결제할수있게 값을 보내줍니다.
        return "pay/payment";
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        System.out.println(request.getPaymentUid());
        System.out.println(request.getOrderUid());
        //결제 완료시주문번호, 고유번호 받아서 리퀘스트로 처리하고 완료시 ajax로 페이지 이동

        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }

    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "pay/success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "pay/fail-payment";
    }

}
