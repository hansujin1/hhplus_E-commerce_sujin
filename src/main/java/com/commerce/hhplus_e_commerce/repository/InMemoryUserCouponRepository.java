package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserCouponRepository implements UserCouponRepository{

    private final Map<Long, UserCoupon> userCouponMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        if (userCoupon.getUser_coupon_id() == null) {
            userCoupon.setUser_coupon_id(idGenerator.getAndIncrement());
        }

        userCouponMap.put(userCoupon.getUser_coupon_id(), userCoupon);

        return userCoupon;
    }

    @Override
    public List<UserCoupon> findAllCoupon(Long user_id) {
        if (user_id == null) {
            throw new IllegalArgumentException("user_id is null");
        }

        return userCouponMap.values().stream()
                .filter(c -> user_id.equals(c.getUser_id()))
                .toList();
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long coupon_id, Long user_id) {
        if (coupon_id == null || user_id == null) {
            throw new IllegalArgumentException("coupon_id or user_id is null");
        }

        return userCouponMap.values().stream()
                .filter(c -> coupon_id.equals(c.getCoupon_id()) && user_id.equals(c.getUser_id()))
                .findFirst();
    }

    @Override
    public void useCoupon(Long coupon_id, Long user_id) {
        UserCoupon coupon = findUserCoupon(coupon_id, user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에게 쿠폰이 존재하지 않습니다."));

        if ("USED".equalsIgnoreCase(coupon.getStatus())) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        coupon.setStatus("USED");
        userCouponMap.put(coupon.getUser_coupon_id(), coupon);
    }

    @Override
    public void couponStatusUsed(Long coupon_id, Long user_id) {
        UserCoupon coupon = findUserCoupon(coupon_id, user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에게 쿠폰이 존재하지 않습니다."));
        coupon.setStatus("Used");
    }
}
