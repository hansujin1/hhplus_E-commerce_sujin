package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        cal.set(2025, Calendar.NOVEMBER, 1);
        Date issuedDate = cal.getTime();

        cal.set(2025, Calendar.NOVEMBER, 30);
        Date expire1 = cal.getTime();

        cal.set(2025, Calendar.NOVEMBER, 16);
        Date expire2 = cal.getTime();

        userCouponRepository.save(new UserCoupon(null,1L,20250202L,"ACTIVE",issuedDate,null,expire1));
        userCouponRepository.save(new UserCoupon(null,2L,20250202L,"USED",issuedDate,new Date(),expire2));
    }



}
