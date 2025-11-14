package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {

    Optional<Coupon> findByCouponId(Long couponId);

}
