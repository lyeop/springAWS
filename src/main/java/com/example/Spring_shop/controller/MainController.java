package com.example.Spring_shop.controller;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.dto.CustomerCenterPostDto;
import com.example.Spring_shop.dto.CustomerSearchDto;
import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.dto.MainItemDto;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.service.CustomerCenterService;
import com.example.Spring_shop.service.ItemService;
import com.example.Spring_shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final MemberService memberService;
    private final CustomerCenterService customerCenterService;

    // 메인 페이지를 처리하는 핸들러입니다.
    @GetMapping("/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        // 모델에 상품 목록과 검색 조건을 추가합니다.
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("items", items);
        return "main"; // main.html 뷰로 이동
    }
    @GetMapping("/members/AddScroll")
    @ResponseBody
    public ResponseEntity<?> getItemsByPage(@RequestParam int page, @RequestParam(required = false) String keyword,
                                            ItemSearchDto itemSearchDto) {
        Pageable pageable = PageRequest.of(page, 3);
        itemSearchDto.setSearchQuery(keyword);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        return ResponseEntity.ok(items);
        //무한스크롤링인데 기존의 있는 아이템뒤에 <id="~~~"> "~~" 이부분에 append를 이용해서 데이터를 이어서 계속 붙임
    }
    @GetMapping(value = "/map")
    public String map(){
        return "api/map";
    }
    //지도

    @GetMapping(value = "/item/items/{itemValue}")
    public String itemsByType(@PathVariable("itemValue") ItemValue itemValue, Optional<Integer> page, Model model, Authentication authentication) {
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setItemValue(itemValue);

        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<MainItemDto> items = itemService.getItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("selectedItemValue", itemValue);
        //enum을 이용해서 상품종류 확인하고 set으로 지정해주고
        //그에맞는 데이터를 찾아서 아이템을 분류해서 page로 만들어서 데이터를 보내줍니다.
        //상품 검색을 해야하기때문에 itemSearchDto도 같이 보냅니다.

        return "item/items";
    }

}
