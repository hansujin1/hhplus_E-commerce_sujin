package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;

import java.util.List;
import java.util.Optional;


public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    List<UserCoupon> findAllCoupon(Long user_id);

    Optional<UserCoupon> findUserCoupon(Long coupon_id, Long user_id);

    void useCoupon(Long coupon_id, Long user_id);

    void couponStatusUsed(Long coupon_id, Long user_id);
}
