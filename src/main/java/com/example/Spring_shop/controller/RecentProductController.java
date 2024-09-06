package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.RecentProduct;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.service.ItemImgService;
import com.example.Spring_shop.service.ItemService;
import com.example.Spring_shop.service.RecentProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecentProductController {
    private final RecentProductService recentProductService;


    @GetMapping("/recentViews")
    @ResponseBody
    public ResponseEntity<List<RecentProduct>> getRecentProducts(HttpServletRequest request) throws IOException {
        // 쿠키에서 최근 본 상품 정보를 읽어옴
        System.out.println("getRecentProducts");
        List<RecentProduct> recentProducts = recentProductService.getRecentProductsFromCookie(request);
        System.out.println("에이작스 구도오오오옹ㅇ"+recentProducts.size());
        for (int i=0; i<recentProducts.size() ;i++){
            if (recentProducts.get(i)!=null){
                System.out.println(recentProducts.get(i).getItemNm()+"최근본상품 이름 순서");
            }
        }

        // 현재 상품의 이미지 URL을 포함한 전체 정보를 반환
        return ResponseEntity.ok(recentProducts);
    }
}
