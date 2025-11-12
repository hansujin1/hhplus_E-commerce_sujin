package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserCouponRepository implements UserCouponRepository{

    private final Map<Long, UserCoupon> userCouponMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        if (userCoupon.getUserCouponId() == null) {
            userCoupon.userCouponId(idGenerator.getAndIncrement());
        }

        userCouponMap.put(userCoupon.getUserCouponId(), userCoupon);

        return userCoupon;
    }

    @Override
    public List<UserCoupon> findAllCoupon(Long user_id) {
        if (user_id == null) {
            throw new IllegalArgumentException("user_id is null");
        }

        return userCouponMap.values().stream()
                .filter(c -> user_id.equals(c.getUserId()))
                .toList();
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long coupon_id, Long user_id) {
        if (coupon_id == null || user_id == null) {
            throw new IllegalArgumentException("coupon_id or user_id is null");
        }

        return userCouponMap.values().stream()
                .filter(c -> coupon_id.equals(c.getCouponId()) && user_id.equals(c.getUserId()))
                .findFirst();
    }

    @Override
    public void useCoupon(Long coupon_id, Long user_id) {
        UserCoupon coupon = findUserCoupon(coupon_id, user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에게 쿠폰이 존재하지 않습니다."));

        if (UserCouponStatus.USED.equals(coupon.getStatus())) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        coupon.use();
        userCouponMap.put(coupon.getUserCouponId(), coupon);
    }

    @Override
    public void couponStatusUsed(Long coupon_id, Long user_id) {
        UserCoupon coupon = findUserCoupon(coupon_id, user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에게 쿠폰이 존재하지 않습니다."));
        coupon.use();
    }
}
