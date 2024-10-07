package com.example.Spring_shop.controller;


import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.constant.Role;
import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.Comment;
import com.example.Spring_shop.entity.CustomerCenterPost;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.CustomerCenterRepository;
import com.example.Spring_shop.repository.MemberRepository;
import com.example.Spring_shop.service.CommentService;
import com.example.Spring_shop.service.CustomerCenterService;
import com.example.Spring_shop.service.ItemService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CustomerCenterController {

    @Autowired
    private CustomerCenterRepository customerCenterPostRepository;
    @Autowired
    private CustomerCenterService customerCenterService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CommentService commentService;

    private final HttpSession httpSession;


    @GetMapping("/customerCenter/write")
    public String customerCenterPost(Model model, Principal principal) {

        if(principal==null){
            model.addAttribute("errorMessage","로그인 후 이용해주세요");
            return "member/memberLoginForm";
        }

        String email = getEmailFromPrincipalOrSession(principal);
        Member member = memberRepository.findByEmail(email);
        //작성하기전에 로그인한 이메일을 통해 맴버를 조회
        CustomerCenterPostFormDto customerCenterPostFormDto =  new CustomerCenterPostFormDto();
        //모델에 양식을 보내주기 위해 양식 객체를 새로 만들어줍니다
        customerCenterPostFormDto.setRole(member.getRole());
        //객체를 만들어줄때 작성자가 user , admin 인지 구별하기 위해 양식안에 맴버의 role 값을 가져와서 넣어줍니다

        model.addAttribute("customerCenterPostFormDto", customerCenterPostFormDto);

        return "customerCenter/customerForm";
    }

    @PostMapping(value = "/customerCenter/new")
    public String customerCenterPostForm(@Valid CustomerCenterPostFormDto customerCenterPostFormDto, Optional<Integer> page,BindingResult bindingResult,
                                         Model model, Principal principal) {
        // @Valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행
        if (bindingResult.hasErrors()) {
            return "customerCenter/customerForm";//다시 작성화면으로 돌려보냄
        }
        //유효성 검사
        try {
            //데이터베이스에 저장
            String email = getEmailFromPrincipalOrSession(principal);
            Member member = memberRepository.findByEmail(email);
            if (member.getRole() == Role.ADMIN){
                customerCenterPostFormDto.setNotice(Notice.ADMIN);
                //작성자가 어드민이면 공지사항으로 작성할수있게 바꿔줍니다
            }
            else {
                customerCenterPostFormDto.setNotice(Notice.USER);
                //작성자가 유저면 문의사항으로 작성할수 있게 바꿉니다.
            }
            customerCenterService.CreateCustomerCenterPost(customerCenterPostFormDto, email);
            //가져온 양식에 받은 데이터를 로그인한 사람의 정보를 담고 글을 작성해서 db에 넣어줍니다.

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customerCenter/customerForm";
        }

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setNotice(Notice.ADMIN);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

        Page<CustomerCenterPostDto> posts = customerCenterService.getCustomerPage(itemSearchDto,pageable);

        model.addAttribute("selectedNotice", Notice.ADMIN);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("dtos", posts);
        //작성 후에 공지사항으로 넘어가기 위해 setNotice로 어드민 지정해주고
        //나오는 갯수 5개
        //검색을 해야하기때문에 itemSearchDto, 넣어주고
        //공지사항 으로 걸러진 데이터를 받아서 페이지로 만들어서 보냅니다.
        //작성하면 -> 무조건 공지사항으로 넘어옵니다.
        return "customerCenter/customerMain";
    }

    @GetMapping("/view/{id}")
    public String viewCustomerCenterPost(@PathVariable Long id, Model model, Principal principal) {
        // id를 이용하여 글 상세 정보를 조회합니다.

        CustomerCenterPost post = customerCenterService.getCustomerCenterPostById(id);
        List<Comment> comments = commentService.getCommentsByPostId(id);

        Member member = null;  // Initialize member as null
        String email = null;

        // Check if the principal is not null (user is logged in)
        if (principal != null) {
            email = getEmailFromPrincipalOrSession(principal);
            member = memberRepository.findByEmail(email); // Find the member based on the logged-in user's email
        }
        // 모델에 글 정보를 추가합니다.
        model.addAttribute("post", post);
        model.addAttribute("member", member);
        model.addAttribute("comments", comments);
        //세션을 통해 로그인한사람 확인하고 조회할때 로그인한사람, 게시글정보를 봅니다.

        return "customerCenter/customerPostView"; // 상세 정보를 보여줄 뷰 페이지로 이동합니다.
    }

    @PostMapping("/customerCenter/delete/{id}")
    public String deleteCustomerCenterPost(@PathVariable Long id,Model model, Optional<Integer> page) {

        customerCenterService.deleteCustomerCenterPostById(id);

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setNotice(Notice.ADMIN);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<CustomerCenterPostDto> posts = customerCenterService.getCustomerPage(itemSearchDto,pageable);
        model.addAttribute("selectedNotice", Notice.ADMIN);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("dtos", posts);

        //게시글 지우고나면 공지사항으로 이동하기위해서 어드민으로 set 해주고
        //게시글갯수 5개  검색하기위해 itemSearchDto 보내주고
        //공지사항만 글을 담아서 보냅니다.
        //공지사항 글만 나오게 됩니다.

        return "customerCenter/customerMain";
    }


    @PostMapping("/customerCenter/update/{id}")
    public String updateCustomerCenterPost(@PathVariable Long id, Model model,
                                           Principal principal) {

        CustomerCenterPost customerCenterPost = customerCenterService.getCustomerCenterPostById(id);
        Notice NTC = customerCenterPost.getNotice();
        // id를 이용하여 글 상세 정보를 조회합니다.
        CustomerCenterPostFormDto customerCenterPostFormDto = customerCenterService.getCustomerCenterPost(id);
        customerCenterPostFormDto.setId(id);
        model.addAttribute("customerCenterPostFormDto", customerCenterPostFormDto);
        model.addAttribute("NTC",NTC);
        //수정하기 위해 게시글 번호 조회하고 양식을 찾아서 보내줍니다.
        return "customerCenter/customerUpdateForm";
    }


    @PostMapping("/customerCenter/updateSave/{id}")
    public String updateSaveCustomerCenterPost(@PathVariable Long id, @Valid CustomerCenterPostFormDto customerCenterPostFormDto,
                                               Model model, BindingResult bindingResult,Optional<Integer> page
    ) {

        if (bindingResult.hasErrors()) {
            System.out.println("오류났어요");
            return "customerCenter/customerUpdateForm";//다시 작성화면으로 돌려보냄
        }
        //유효성 검사
        try {
            //데이터베이스에 업데이트
            customerCenterService.updateCustomerCenterPost(customerCenterPostFormDto);
        //유효성 통과시 게시글의 정보를 업데이트 합니다.
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            System.out.println("여기 오류에요");
            return "customerCenter/customerUpdateForm";
        }

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setNotice(Notice.ADMIN);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<CustomerCenterPostDto> posts = customerCenterService.getCustomerPage(itemSearchDto,pageable);
        model.addAttribute("selectedNotice", Notice.ADMIN);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("dtos", posts);
        //업데이트한 이후에는 공지사항으로 set 바꿔주고
        //갯수 5개 , 검색하기위해 itemSearchDto 넣어주고
        //공지사항의 글만 조회해서 보내고
        //공지사항으로 이동합니다.

        return "customerCenter/customerMain";
    }
    @GetMapping(value = "/customerCenter/{notice}")
    public String customerCenter(@PathVariable("notice") Notice notice, Optional<Integer> page, Model model,Principal principal) {

        Role role = Role.Newbie;
        if (principal!=null){
            String email = getEmailFromPrincipalOrSession(principal);
            Member member = memberRepository.findByEmail(email);
            role = member.getRole();
        }

        // 데이터베이스에서 모든 게시물을 가져옴
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setNotice(notice);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<CustomerCenterPostDto> posts = customerCenterService.getCustomerPage(itemSearchDto,pageable);
        //받아온 notice를 가지고 set으로 정해주고 그값을 통해서 조회해서 데이터를 빼옵니다
        // 문의사항(user) 공지사항(admin) 정하고 그거에 맞춰서 조회해서 페이지를 만들어서 값을 넣어줍니다.
        // Entity를 DTO로 변환하여 모델에 추가

        model.addAttribute("dtos", posts);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("selectedNotice", role);
        model.addAttribute("NTC", notice);
        //헤더에서 문의사항을 누를시 enum을 이용해서 notice가 유저(문의사항) 문의사항 탭으로 이동
        //헤더에서 공지사항을 누를시 enum을 이용해서 notice가 어드민(공지사항) 탭으로 이동)

        return "customerCenter/customerMain";
    }

    @GetMapping(value = {"/Cpost/{page}/{notice}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             @PathVariable("notice") Notice notice, Model model){
        // page.isPresent() -> page 값 있어?
        // 어 값 있어 page.get() 아니 값 없어 0
        // 페이지당 사이즈 5 -> 5개만 나옵니다. 6개 되면 페이지 바뀜
        // 페이지 번호가 있으면 해당 페이지를, 없으면 0 페이지를 요청합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

        // 상품 목록을 조회합니다.
        Page<CustomerCenterPostDto> posts = customerCenterService.getCustomerPage(itemSearchDto,pageable);

        model.addAttribute("dtos", posts);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("NTC", notice);

        //페이지네비게이션으로 옮길시 받아온 notice(공지사항,문의사항) 값을 가지고
        //받아온 값을 이용해 공지사항 페이지 이동 or 문의사항 페이지 이동

        return "customerCenter/customerMain";
    }



    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }

}