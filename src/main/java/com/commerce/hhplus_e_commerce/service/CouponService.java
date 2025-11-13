package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.locks.ReentrantLock;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final ReentrantLock issueLock = new ReentrantLock(true);

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;

    public void validateCoupon(Long userId,Long couponId) {
        if(couponId == null){
            throw new IllegalArgumentException("쿠폰 ID가 비어있습니다.");
        }

        if(userId == null){
            throw new IllegalArgumentException("유저ID가 없습니다.");
        }

        UserCoupon userCoupon = userCouponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다: " + couponId));

        if (!userCoupon.isValid()) {
            throw new IllegalStateException("만료되었거나 이미 사용된 쿠폰입니다.");
        }
    }

    public int getDiscountAmount(Long couponId, int totalPrice) {

        Coupon coupon = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰 정책을 찾을 수 없습니다: " + couponId));

        return coupon.calculateDiscount(totalPrice);
    }

    //쿠폰 사용완료
    public void consumeOnPayment(Long userId, Long couponId) {
        UserCoupon userCoupon = userCouponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다: " + couponId));

        userCoupon.use();
        userCouponRepository.save(userCoupon);
    }

    //결제 실패로 원복
    public void restoreCouponStatus(Long userId, Long couponId) {
        UserCoupon userCoupon = userCouponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다: " + couponId));

        if(userCoupon.getStatus().equals(UserCouponStatus.USED)){
            userCoupon.activate();
            userCouponRepository.save(userCoupon);
        }
    }

    public UserCoupon issueCoupon(Long userId, Long couponId) {

        issueLock.lock();
        try {

            Coupon coupon = couponRepository.findByCouponId(couponId)
                    .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다."));

            // 중복 발급 방지
            if (userCouponRepository.findUserCoupon(userId, couponId).isPresent()) {
                throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
            }

            // 선착순 가능 여부 체크 + 발급 수량 증가
            coupon.issue();
            couponRepository.save(coupon);


            UserCoupon userCoupon = new UserCoupon(
                    null,
                    couponId,
                    userId,
                    UserCouponStatus.ACTIVE,
                    LocalDate.now().plusDays(coupon.getValidDays())
            );

            return userCouponRepository.save(userCoupon);

        } finally {
            issueLock.unlock();
        }
    }
}
