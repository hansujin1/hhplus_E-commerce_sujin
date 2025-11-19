package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Slf4j
@Component
public class UserCouponDataInitializer {

    private final UserCouponRepository userCouponRepository;

    public UserCouponDataInitializer(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    @PostConstruct
    public void init(){

        LocalDate expire1    = LocalDate.of(2025, 11, 30);
        LocalDate expire2    = LocalDate.of(2025, 11, 16);

        userCouponRepository.save(new UserCoupon(1L,20250202L, UserCouponStatus.ACTIVE,expire1));
        userCouponRepository.save(new UserCoupon(2L,20250202L,UserCouponStatus.USED,expire2));
    }



}
