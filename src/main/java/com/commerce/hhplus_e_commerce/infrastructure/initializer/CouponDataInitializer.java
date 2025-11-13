package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import com.commerce.hhplus_e_commerce.domain.enums.DiscountType;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;


@Slf4j
@Component
public class CouponDataInitializer {

    private final CouponRepository couponRepository;

    public CouponDataInitializer(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @PostConstruct
    public void init(){
        log.info("coupon 정보 초기 셋팅하기");

        LocalDate startDate1 = LocalDate.of(2025, Month.JANUARY, 1);
        LocalDate endDate1   = LocalDate.of(2025, Month.DECEMBER, 31);

        couponRepository.save(new Coupon("첫구매 15% 할인 쿠폰", 0.15, DiscountType.FIXED,
                1000,100,startDate1,endDate1,30, CouponStatus.ISSUING
        ));


        LocalDate startDate2 = LocalDate.of(2025, Month.FEBRUARY, 1);
        LocalDate endDate2   = LocalDate.of(2025, Month.JUNE, 30);

        couponRepository.save(new Coupon("겨울대비 20,000원 할인 쿠폰",20000.0,
                DiscountType.RATE,500,50,startDate2,endDate2,15, CouponStatus.ISSUING
        ));
    }



}
