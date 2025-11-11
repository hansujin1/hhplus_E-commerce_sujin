package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCouponRepository implements CouponRepository{

    private final Map<Long, Coupon> couponMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public Coupon save(Coupon coupon) {
        if(coupon.getCoupon_id() == null){
            coupon.couponId(idGenerator.incrementAndGet());
        }

        couponMap.put(coupon.getCoupon_id(), coupon);

        return coupon;
    }

    @Override
    public List<Coupon> findAllCoupon() {
        return couponMap.values().stream().toList();
    }

    @Override
    public Optional<Coupon> findByCouponId(Long coupon_id) {
        if(coupon_id == null){
            throw new RuntimeException("coupon_id is null");
        }

        return Optional.ofNullable(couponMap.get(coupon_id));
    }

    // 쿠폰 발급 시 발급 수량 증가
    @Override
    public void issueCoupon(Long couponId) {
        Coupon coupon = couponMap.get(couponId);
        if (coupon == null) {
            throw new IllegalArgumentException("Coupon not found: " + couponId);
        }

        if (coupon.getIssued_amount() >= coupon.getTotal_quantity()) {
            throw new IllegalStateException("Coupon is sold out.");
        }

        coupon.issue();
    }

    // 사용 가능 여부 확인
    @Override
    public boolean isAvailable(Long couponId) {
        Coupon coupon = couponMap.get(couponId);
        if (coupon == null) return false;

        Date now = new Date();
        return coupon.getStatus().equals("ACTIVE") &&
                now.after(coupon.getStart_date()) &&
                now.before(coupon.getEnd_date()) &&
                coupon.getIssued_amount() < coupon.getTotal_quantity();
    }
}
