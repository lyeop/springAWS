package com.example.Spring_shop.controller;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.entity.ItemImg;
import com.example.Spring_shop.repository.ItemRepository;
import com.example.Spring_shop.service.BidService;
import com.example.Spring_shop.service.ItemImgService;
import com.example.Spring_shop.service.ItemService;
import com.example.Spring_shop.service.RecentProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final RecentProductService recentProductService;


    // 새로운 상품 등록 폼을 보여줍니다.
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
        //관리자가 상품등록할때 새로운 객체를 받아서 글쓰기 양식을 보내줍니다.
    }

    // 새로운 상품을 등록합니다.
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        // 입력 값 검증에서 오류가 발생하면 다시 상품 등록 폼을 보여줍니다.
        if (bindingResult.hasErrors()){
            return "/item/itemForm";
        }
        //유효성 검사

        // 첫 번째 상품 이미지는 필수 입력 값입니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }
        try {
            // 상품 정보를 저장합니다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }
        return "redirect:/";
    }

    // 상품 상세 정보를 조회합니다.
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }
        catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "/item/itemForm";
        }
        //상품 수정
        return "/item/itemForm";
    }

    // 상품 정보를 수정합니다.
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model){

        // 입력 값 검증에서 오류가 발생하면 다시 상품 수정 폼을 보여줍니다.
        if (bindingResult.hasErrors()){
            return "/item/itemForm";
        }
        // 첫 번째 상품 이미지는 필수 입력 값입니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }
        try {
            // 상품 정보를 수정합니다.
            itemService.updateItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/"; // 다시 실행 /
    }

    //value 2개인 이유
    //1. 네비게이션에서 상품관리 클릭하면 나오는거
    //2. 상품관리안에서 페이지 이동할때 받는거
    // 관리자 페이지에서 상품 목록을 관리합니다.
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){
        // page.isPresent() -> page 값 있어?
        // 어 값 있어 page.get() 아니 값 없어 0
        // 페이지당 사이즈 5 -> 5개만 나옵니다. 6개 되면 페이지 바뀜
        // 페이지 번호가 있으면 해당 페이지를, 없으면 0 페이지를 요청합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

        // 상품 목록을 조회합니다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        //상품관리 페이지 상품들을 조회해서 페이지로 만들고
        //검색도 해야하기떄문에 itemSearchDto 넣어주고
        //게시글갯수는 5개이상 넘어가면 페이지가 생김

        return "/item/itemMng";
    }

    // 상품 상세 정보를 조회합니다.
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId")Long itemId, HttpServletRequest request, HttpServletResponse response){

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        // 아이템 이미지 URL을 가져와서 쿠키에 최근 본 상품으로 저장합니다
        String recentImage = itemImgService.selectProductImageUrlByProductId(itemId);

        recentProductService.saveRecentProductToCookie(itemId, response, request, recentImage, itemFormDto.getItemNm());

        List<RecentProduct> recentProducts = recentProductService.getRecentProductsFromCookie(request);
        for (int i=0; i<recentProducts.size(); i++){
            System.out.println("인덱스가 제대로 바뀌는가"+ recentProducts.get(i).getItemNm());
        }

        return "/item/itemDtl";
        //아이템 상세정보
    }
    @GetMapping(value = {"/choice/{itemValue}/{page}"})
    public String pageItemValue(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                                @PathVariable("itemValue") ItemValue itemValue, Model model){
        // page.isPresent() -> page 값 있어?
        // 어 값 있어 page.get() 아니 값 없어 0
        // 페이지당 사이즈 5 -> 5개만 나옵니다. 6개 되면 페이지 바뀜
        // 페이지 번호가 있으면 해당 페이지를, 없으면 0 페이지를 요청합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);


        Page<MainItemDto> items = itemService.getItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("selectedItemValue", itemValue);
        //enum값 받은 걸로 상품종류 구분하고 , 페이지네비게이션 달아서 5개이상이면 페이지 생기고
        //조회한 데이터로 출력
        return "item/items";
    }
    @GetMapping("/listItem")
    public ResponseEntity<List<Item>> listItem(){
        List<Item> itemList = new ArrayList<>();
        List<Item> itemList1 = itemRepository.findAll();
        for (int i = itemList1.size() - 1; i >= 0; i--) {
            if (itemList.size() == 5) {
                break; // itemList에 5개의 아이템이 추가되면 루프 종료
            }
            if (itemList1.get(i) != null) {
                itemList.add(itemList1.get(i)); // itemList1의 마지막부터 아이템 추가
            }
        }

        return ResponseEntity.ok(itemList);
    }

}
