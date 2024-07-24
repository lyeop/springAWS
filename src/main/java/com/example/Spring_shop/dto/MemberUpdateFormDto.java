package com.example.Spring_shop.dto;


import com.example.Spring_shop.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class MemberUpdateFormDto {

    @NotEmpty (message = "우편번호는 필수 입력 값입니다.")
    private String zipcode;
    @NotEmpty (message ="도로명 주소는 필수 입력 값입니다.")
    private String streetAdr;
    @NotEmpty(message = "상세 주소는 필수 입력 값입니다.")
    private String detailAdr;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

}
