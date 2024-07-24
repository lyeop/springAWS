package com.example.Spring_shop.dto;

import com.example.Spring_shop.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email
    private String email;

    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;
    @NotEmpty (message = "우편번호는 필수 입력 값입니다.")
    private String zipcode;
    @NotEmpty (message ="도로명 주소는 필수 입력 값입니다.")
    private String streetAdr;
    @NotEmpty(message = "상세 주소는 필수 입력 값입니다.")
    private String detailAdr;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;
}
