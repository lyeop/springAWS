package com.example.Spring_shop.entity;


import com.example.Spring_shop.constant.Role;
import com.example.Spring_shop.dto.MemberFormDto;
import com.example.Spring_shop.dto.SecessionReasonDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@Table
@ToString
public class SecessionReason {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //알아서 설정
    @Column
    private String secessionReason;
    @Column
    private String detailReason;

    public SecessionReason() {
    }

    public static SecessionReason createSecessionReason(SecessionReasonDto secessionReasonDto) {

        SecessionReason secessionReason = new SecessionReason();

        secessionReason.setSecessionReason(secessionReasonDto.getSecessionReason());
        secessionReason.setDetailReason(secessionReasonDto.getDetailReason());

        return secessionReason;
    }
}
