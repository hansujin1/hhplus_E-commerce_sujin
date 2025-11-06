package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;

    public void validateCoupon(Long userId,Long couponId) {
        if(couponId == null){
            throw new IllegalArgumentException("쿠폰 ID가 비어있습니다.");
        }

        if(userId == null){
            throw new IllegalArgumentException("유저ID가 없습니다.");
        }

        UserCoupon userCoupon = userCouponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다: " + couponId));

        if (!userCoupon.isValid()) {
            throw new IllegalStateException("만료되었거나 이미 사용된 쿠폰입니다.");
        }
    }

    public int getDiscountAmount(Long couponId, int totalPrice) {

        Coupon coupon = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰 정책을 찾을 수 없습니다: " + couponId));

        return coupon.calculateDiscount(totalPrice);
    }
}
