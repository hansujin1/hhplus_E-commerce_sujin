package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;

import java.util.List;
import java.util.Optional;


public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    List<UserCoupon> findAllCoupon(Long userId);

    Optional<UserCoupon> findUserCoupon(Long couponId, Long userId);

    void useCoupon(Long couponId, Long userId);

}
