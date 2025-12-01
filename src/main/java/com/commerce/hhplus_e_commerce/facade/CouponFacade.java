package com.commerce.hhplus_e_commerce.facade;

import com.commerce.hhplus_e_commerce.config.DistributedLock;
import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    @DistributedLock(
        key = "'coupon:issue:' + #couponId",
        waitTime = 5L,
        leaseTime = 3L
    )
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        return couponService.issueCoupon(userId, couponId);
    }

    public void validateCoupon(Long userId, Long couponId) {
        couponService.validateCoupon(userId, couponId);
    }

    public int getDiscountAmount(Long couponId, int totalPrice) {
        return couponService.getDiscountAmount(couponId, totalPrice);
    }

    public void consumeOnPayment(Long userId, Long couponId) {
        couponService.consumeOnPayment(userId, couponId);
    }

    public void restoreCouponStatus(Long userId, Long couponId) {
        couponService.restoreCouponStatus(userId, couponId);
    }
}