package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Coupon;

import java.util.List;
import java.util.Optional;


public interface CouponRepository {

    Coupon save(Coupon coupon);

    List<Coupon> findAllCoupon();

    Optional<Coupon> findByCouponId(Long couponId);

    void issueCoupon(Long couponId);

    boolean isAvailable(Long couponId);
}
