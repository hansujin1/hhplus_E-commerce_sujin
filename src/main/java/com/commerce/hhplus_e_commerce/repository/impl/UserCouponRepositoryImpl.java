package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.UserCouponJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository userCouponJpaRepository;


    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public List<UserCoupon> findAllCoupon(Long userId) {
        return userCouponJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long couponId, Long userId) {
        return userCouponJpaRepository.findByCouponIdAndUserId(couponId,userId);
    }
    
    @Override
    public Optional<UserCoupon> findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId);
    }

    @Override
    @Transactional
    public void useCoupon(Long couponId, Long userId) {
        UserCoupon userCoupon = userCouponJpaRepository.findByCouponIdAndUserId(couponId, userId)
                .orElseThrow(() -> new IllegalStateException("사용자 쿠폰을 찾을 수 없습니다."));

        userCoupon.use();

        userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public long count() {
        return userCouponJpaRepository.count();
    }

}
