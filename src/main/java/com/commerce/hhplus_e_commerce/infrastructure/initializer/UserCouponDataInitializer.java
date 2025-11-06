package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


@Slf4j
@Component
public class UserCouponDataInitializer {

    private final UserCouponRepository userCouponRepository;

    public UserCouponDataInitializer(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    @PostConstruct
    public void init(){

        Calendar cal = Calendar.getInstance();

        LocalDate issuedDate = LocalDate.of(2025, 11, 1);
        LocalDate expire1    = LocalDate.of(2025, 11, 30);
        LocalDate expire2    = LocalDate.of(2025, 11, 16);

        userCouponRepository.save(new UserCoupon(1L,1L,20250202L, UserCouponStatus.ACTIVE,issuedDate,null,expire1));
        userCouponRepository.save(new UserCoupon(2L,2L,20250202L,UserCouponStatus.USED,issuedDate, LocalDate.now(),expire2));
    }



}
