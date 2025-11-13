package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCouponRepository implements CouponRepository{

    private final Map<Long, Coupon> couponMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public Coupon save(Coupon coupon) {
        if(coupon.getCouponId() == null){
            coupon.couponId(idGenerator.incrementAndGet());
        }

        couponMap.put(coupon.getCouponId(), coupon);

        return coupon;
    }

    @Override
    public List<Coupon> findAllCoupon() {
        return couponMap.values().stream().toList();
    }

    @Override
    public Optional<Coupon> findByCouponId(Long couponId) {
        if(couponId == null){
            throw new RuntimeException("couponId is null");
        }

        return Optional.ofNullable(couponMap.get(couponId));
    }

    // 쿠폰 발급 시 발급 수량 증가
    @Override
    public void issueCoupon(Long couponId) {
        Coupon coupon = couponMap.get(couponId);
        if (coupon == null) {
            throw new IllegalArgumentException("Coupon not found: " + couponId);
        }

        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            throw new IllegalStateException("Coupon is sold out.");
        }

        coupon.issue();
    }

    // 사용 가능 여부 확인
    @Override
    public boolean isAvailable(Long couponId) {
        Coupon coupon = couponMap.get(couponId);
        if (coupon == null) return false;

        LocalDate now = LocalDate.now();
        return coupon.getStatus().equals(CouponStatus.ISSUING) &&
                now.isAfter(coupon.getStartDate()) &&
                now.isBefore(coupon.getEndDate()) &&
                coupon.getIssuedQuantity() < coupon.getTotalQuantity();
    }
}
