package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import com.commerce.hhplus_e_commerce.domain.enums.DiscountType;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;


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

        Calendar cal = Calendar.getInstance();
        
        cal.set(2025, Calendar.JANUARY, 1);
        Date startDate1 = cal.getTime();
        cal.set(2025, Calendar.DECEMBER, 31);
        Date endDate1 = cal.getTime();

        couponRepository.save(new Coupon(1L, "첫구매 15% 할인 쿠폰", 0.15, DiscountType.FIXED,
                1000,100,startDate1,endDate1,30, CouponStatus.ISSUING
        ));


        cal.set(2025, Calendar.FEBRUARY, 1);
        Date startDate2 = cal.getTime();
        cal.set(2025, Calendar.JUNE, 30);
        Date endDate2 = cal.getTime();

        couponRepository.save(new Coupon(2L,"겨울대비 20,000원 할인 쿠폰",20000.0,
                DiscountType.RATE,500,50,startDate2,endDate2,15, CouponStatus.ISSUING
        ));
    }



}
