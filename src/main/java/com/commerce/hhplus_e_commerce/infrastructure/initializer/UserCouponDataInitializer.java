package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Slf4j
@Component
@DependsOn({"userDataInitializer", "couponDataInitializer"})
public class UserCouponDataInitializer {

    private final UserCouponRepository userCouponRepository;

    public UserCouponDataInitializer(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    @PostConstruct
    public void init(){
        if (userCouponRepository.count() > 0) {
            log.info("UserCoupon 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("UserCoupon 정보 초기 셋팅하기");
        LocalDate expire1    = LocalDate.of(2025, 11, 30);
        LocalDate expire2    = LocalDate.of(2025, 11, 16);

        // userId를 올바른 값으로 수정 (1L, 2L)
        userCouponRepository.save(new UserCoupon(1L, 1L, UserCouponStatus.ACTIVE, expire1));
        userCouponRepository.save(new UserCoupon(2L, 1L, UserCouponStatus.USED, expire2));
    }



}
