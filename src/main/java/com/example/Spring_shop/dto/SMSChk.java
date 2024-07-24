package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SMSChk {
    private String phoneNumber;
    private String verificationCode;
}