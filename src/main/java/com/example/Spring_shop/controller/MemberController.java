package com.example.Spring_shop.controller;

import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.entity.SecessionReason;
import com.example.Spring_shop.repository.MemberRepository;
import com.example.Spring_shop.service.CustomerCenterService;
import com.example.Spring_shop.service.MailService;
import com.example.Spring_shop.service.MemberService;
import com.example.Spring_shop.service.SecessionReasonService;
import com.example.Spring_shop.util.RoleToNoticeConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final SecessionReasonService secessionReasonService;
    private final CustomerCenterService customerCenterService;
    private final Map<String, String> phoneVerificationCodes = new HashMap<>();

    String confirm = "";
    Boolean confirmCheck = false;
    Boolean confirmFindCheck = false;
    Boolean confirmSMSCheck = true;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "/member/memberForm";
    }

    //----------------------------------------마이 페이지----------------------------------------------------

    @GetMapping(value = "/mypage")
    public String memberMyPage(Model model, Principal principal){

        String email = getEmailFromPrincipalOrSession(principal);
        //메소드를 이용해서 소셜로그인, 일반로그인을 분류합니다
        Member member = memberRepository.findByEmail(email);
        //분류한 데이터 email을 가지고 멤버를 찾습니다.
        MemberUpdateFormDto memberUpdateFormDto = new MemberUpdateFormDto();
        memberUpdateFormDto.setZipcode(member.getAddress().getZipcode());
        memberUpdateFormDto.setStreetAdr(member.getAddress().getStreetAdr());
        memberUpdateFormDto.setDetailAdr(member.getAddress().getDetailAdr());
        //업데이트 객체를 만들어주고 맴버가 가지고 있는 데이터 상세주소,가져오고 set으로 지정해줍니다
        memberUpdateFormDto.setTel(member.getTel());

        model.addAttribute("member",member);
        model.addAttribute("memberUpdateFormDto", memberUpdateFormDto);
        //모델로 로그인한 게졍 , 업데이트할수있는 객체를 담아서 보내줍니다.


        return "/member/memberMyPage";
    }


    @PostMapping (value = "/update/{id}")
    public String memberUpdate(@PathVariable Long id, @Valid MemberUpdateFormDto memberUpdateFormDto,
                               BindingResult bindingResult,Model model, Principal principal){

        if (bindingResult.hasErrors()) {
            return "/member/memberMyPage";//다시 회원가입으로 돌려보닙니다.
        }
        try {
            //데이터베이스에 업데이트
            memberService.updateMember(memberUpdateFormDto, id);

        } catch (Exception e){
            model.addAttribute("errorMessage", "정보 수정 중 에러가 발생하였습니다.");

            String email = getEmailFromPrincipalOrSession(principal);
            Member member = memberRepository.findByEmail(email);

            MemberUpdateFormDto memberUpdateFormDto1 = new MemberUpdateFormDto();
            memberUpdateFormDto.setZipcode(member.getAddress().getZipcode());
            memberUpdateFormDto.setStreetAdr(member.getAddress().getStreetAdr());
            memberUpdateFormDto.setDetailAdr(member.getAddress().getDetailAdr());

            memberUpdateFormDto.setTel(member.getTel());

            model.addAttribute("member",member);
            model.addAttribute("memberUpdateFormDto", memberUpdateFormDto1);

            return "/member/memberMyPage";
        }

        String email = getEmailFromPrincipalOrSession(principal);
        Member member = memberRepository.findByEmail(email);

        MemberUpdateFormDto memberUpdateFormDto1 = new MemberUpdateFormDto();
        memberUpdateFormDto.setZipcode(member.getAddress().getZipcode());
        memberUpdateFormDto.setStreetAdr(member.getAddress().getStreetAdr());
        memberUpdateFormDto.setDetailAdr(member.getAddress().getDetailAdr());

        memberUpdateFormDto.setTel(member.getTel());
        //이메일 조회하고 DTo를 이용해서업데이트 해줍니다.
        model.addAttribute("member",member);
        model.addAttribute("memberUpdateFormDto", memberUpdateFormDto1);

        return "/member/memberMyPage";
    }

    //----------------------------------------------회원가입-------------------------------------------

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                             Model model) {
        // @Valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행
        if (bindingResult.hasErrors()) {
            return "/member/memberForm";//다시 회원가입으로 돌려보닙니다.
        }
        if (!confirmCheck) {
            //메일인증 안할시 모델로 에러메세지 보내서 이메일 인증하라고 보냄
            model.addAttribute("errorMessage", "이메일 인증을 하세요.");
            return "/member/memberForm";
        }
        try {
            //Member 객체 생성

            Member member = Member.createMember(memberFormDto, passwordEncoder);
            //데이터베이스에 저장
            memberService.saveMember(member);
            confirmCheck = false;

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/member/memberForm";
        }
        return "redirect:/";
    }

    //--------------------------------------로그인--------------------------------------------------

    @GetMapping(value = "/login")
    public String loginMember(Authentication authentication, Model model) {

        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    //---------------------------회원가입 이메일 인증 ----------------------------------------------------

    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email)
            throws Exception {
        confirm = mailService.sendSimpleMessage(email);

        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
        //이메일인즈을할때 받아온 email로 이용해서 ajax를 통해 이메일 보냅니다.
    }

    @PostMapping("/{code}/codeCheck")
    public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code") String code, Model model)
            throws Exception {

        if (confirm.equals(code)) {
            confirmCheck = true;
            return new ResponseEntity<String>("인증 성공", HttpStatus.OK);
            //java에서 정해놓은 랜덤값이 보낸 인증 값이랑 같을 경우 ajax로 인증이 완료 되었다고 보냄니다.
        } else {
            return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    // --------------------------------------------------비밀번호 찾기 ------------------------------------------

    @GetMapping(value = "/findpassword")
    public String memberFindPassword(Model model){

        MemberEmailDto memberEmailDto = new MemberEmailDto();

        model.addAttribute("memberEmailDto",memberEmailDto);

        return "/member/memberFindPassword";

    }


    @PostMapping(value = "/FindPassword2")
    public String memberFindPassword2(@Valid MemberEmailDto memberEmailDto, BindingResult bindingResult,
                                      Model model) {

        //유효성 검사
        if (bindingResult.hasErrors()) {

            MemberEmailDto memberEmailDto1=new MemberEmailDto();
            model.addAttribute("memberEmailDto",memberEmailDto1);

            return "/member/memberFindPassword";
        }
        //인증이 안되어있으면 다시 돌려보내기
        if (!confirmFindCheck) {
            model.addAttribute("errorMessage", "이메일 인증을 하세요.");
            MemberEmailDto memberEmailDto1=new MemberEmailDto();
            model.addAttribute("memberEmailDto",memberEmailDto1);
            return "/member/memberFindPassword";
        }
        Member member = memberRepository.findByEmail(memberEmailDto.getEmail());
        MemberPasswordDto memberPasswordDto = new MemberPasswordDto();
        memberPasswordDto.setEmail(member.getEmail());

        model.addAttribute("member",member);
        model.addAttribute("memberPasswordDto",memberPasswordDto);

        return "/member/memberFindPassword2";
    }

    @PostMapping(value = "/memberUpdatePassword")
    public String memberUpdatePassword1(@Valid MemberPasswordDto memberPasswordDto, BindingResult bindingResult,
                                        Model model) {

        //유효성 검사
        if (bindingResult.hasErrors()) {

            MemberPasswordDto memberPasswordDto1=new MemberPasswordDto();
            model.addAttribute("memberPasswordDto",memberPasswordDto1);

            return "/member/memberFindPassword2";
        }

        memberService.updatePassword(memberPasswordDto, passwordEncoder);

        return "/member/memberLoginForm";
    }

    @PostMapping("/{email}/emailFindConfirm")
    public @ResponseBody ResponseEntity emailFindConfirm(@PathVariable("email") String email)
            throws Exception {

        confirm = mailService.sendSimpleMessage(email);

        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }


    @PostMapping("/{code}/codeFindCheck")
    public @ResponseBody ResponseEntity codeFindConfirm(@PathVariable("code") String code, Model model)
            throws Exception {

        if (confirm.equals(code)) {

            confirmFindCheck = true;

            return new ResponseEntity<String>("인증 성공", HttpStatus.OK);

        } else {
            return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    //---------------------------------------------로그인 정보 찾기 ---------------------------------------------

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }

    @RequestMapping(value = "/phoneCheck", method = RequestMethod.GET)
    @ResponseBody
    public String sendVerificationCode(@RequestParam String phoneNumber) {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr.append(ran);
        }

        String verificationCode = numStr.toString();

        phoneVerificationCodes.put(phoneNumber, verificationCode);

        // 실제로 SMS를 보내는 부분
        memberService.certifiedPhoneNumber(phoneNumber, verificationCode);

        return "인증번호가 전송되었습니다.";
    }

    @PatchMapping("/verifyCode")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestBody SMSChk smsChk) {
        String storedCode = phoneVerificationCodes.get(smsChk.getPhoneNumber());

        System.out.println("핸드폰 번호"+ smsChk.getPhoneNumber());
        System.out.println("인증해야할 코드"+ storedCode);
        System.out.println("인증코드"+smsChk.getVerificationCode());

        if (storedCode != null && storedCode.equals(smsChk.getVerificationCode())) {
            confirmSMSCheck = true;
            return ResponseEntity.badRequest().body("인증번호 확인 완료");
        } else {
            return ResponseEntity.ok(smsChk.getVerificationCode());
        }
    }

    //-------------------------------------------------탈퇴 -----------------------------------------------
    @GetMapping (value = "/deleteMember")
    public String deleteMember (Model model,Principal principal){

        String email = getEmailFromPrincipalOrSession(principal);

        SecessionReasonDto secessionReasonDto = new SecessionReasonDto();
        secessionReasonDto.setEmail(email);
        System.out.println(email);

        model.addAttribute("secessionReasonDto", secessionReasonDto);

        return "/member/memberDelete";
    }

    @PostMapping(value = "/deleteMember2")
    public String deleteMember2(SecessionReasonDto secessionReasonDto, HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication, RedirectAttributes redirectAttributes){

        SecessionReason secessionReason = SecessionReason.createSecessionReason(secessionReasonDto);
        secessionReasonService.saveSecessionReason(secessionReason);

        memberService.deleteMember(secessionReasonDto.getEmail());

        // 로그아웃 처리
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);


        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    //-----------------------------나의 문의 내역 찾기 --------------------------------
    @GetMapping(value = "/myPost")
    public String memberMyPost(Optional<Integer> page, Model model, Authentication authentication, Principal principal) {

        // 현재 로그인한 사용자의 이메일을 가져옵니다.
        String currentUsername = getEmailFromPrincipalOrSession(principal);
        Member member = memberRepository.findByEmail(currentUsername);
        Notice notice = RoleToNoticeConverter.convertRoleToNotice(member.getRole());

        // 데이터베이스에서 사용자가 작성한 게시물만 가져오도록 합니다.
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setNotice(notice);
        itemSearchDto.setAuthorEmail(currentUsername); // 현재 사용자의 이메일을 검색 조건으로 추가

        System.out.println(notice);
        System.out.println(currentUsername);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<CustomerCenterPostDto> posts = customerCenterService.getMyPost(itemSearchDto, pageable);

        // 결과를 모델에 추가
        model.addAttribute("dtos", posts);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("selectedNotice", notice);
        model.addAttribute("NTC", notice);

        return "/customerCenter/customerMain";
    }
}
