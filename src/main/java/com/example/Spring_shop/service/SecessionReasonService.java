package com.example.Spring_shop.service;


import com.example.Spring_shop.entity.SecessionReason;
import com.example.Spring_shop.repository.SecessionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SecessionReasonService {
    private final SecessionReasonRepository secessionReasonRepository; //자동 주입됨

    public SecessionReason saveSecessionReason(SecessionReason secessionReason) {

        return secessionReasonRepository.save(secessionReason); // 데이터베이스에 저장을 하라는 명령
    }

}
