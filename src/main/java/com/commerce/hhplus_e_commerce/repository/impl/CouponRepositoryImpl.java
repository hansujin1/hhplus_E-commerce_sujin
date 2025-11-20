package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.CouponJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;


    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupon() {
        return couponJpaRepository.findAll();
    }

    @Override
    public Optional<Coupon> findByCouponId(Long couponId) {
        return couponJpaRepository.findByCouponId(couponId);
    }

    @Override
    @Transactional
    public void issueCoupon(Long couponId) {
        Coupon coupon = couponJpaRepository.findByCouponId(couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다."));

        coupon.issue();
        couponJpaRepository.save(coupon);
    }

    @Override
    @Transactional
    public boolean isAvailable(Long couponId) {
        return couponJpaRepository.findByCouponId(couponId)
                .map(Coupon::canIssue)
                .orElse(false);
    }

    @Override
    public Optional<Coupon> findByCouponIdWithLock(Long couponId) {
        return couponJpaRepository.findByCouponIdWithLock(couponId);
    }
}
