package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserCouponJpaRepository extends JpaRepository<UserCoupon,Long> {

    List<UserCoupon> findByUserId(Long userId);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);

}
