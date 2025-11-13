package com.commerce.hhplus_e_commerce.tdd.service;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.repository.UserCouponRepository;
import com.commerce.hhplus_e_commerce.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("쿠폰 service 테스트 코드")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("쿠폰 발급 성공 - 중복 X, 잔여 수량 있음")
    void issueCoupon_success() {

        Long userId = 1L;
        Long couponId = 100L;

        Coupon coupon = mock(Coupon.class);
        when(couponRepository.findByCouponId(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.findUserCoupon(userId, couponId)).thenReturn(Optional.empty());
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCoupon issued = couponService.issueCoupon(userId, couponId);

        assertThat(issued.getCouponId()).isEqualTo(couponId);
        assertThat(issued.getUserId()).isEqualTo(userId);
        assertThat(issued.getStatus()).isEqualTo(UserCouponStatus.ACTIVE);
        verify(coupon).issue(); // 쿠폰 발급 수량 증가 로직 호출 확인
        verify(couponRepository).save(coupon);
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급받은 쿠폰")
    void issueCoupon_alreadyIssued() {
        Long userId = 1L;
        Long couponId = 100L;
        LocalDate issued_date = LocalDate.now();

        when(userCouponRepository.findUserCoupon(userId, couponId))
                .thenReturn(Optional.of(new UserCoupon(1L
                                                       ,couponId
                                                       ,userId
                                                       ,UserCouponStatus.ACTIVE
                                                       ,issued_date.plusDays(30))));


        assertThatThrownBy(() -> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 발급받은 쿠폰");
    }

    @Test
    @DisplayName("쿠폰 검증 실패 - 만료 or 사용됨")
    void validateCoupon_expired() {
        Long userId = 1L;
        Long couponId = 100L;

        UserCoupon expiredCoupon = mock(UserCoupon.class);
        when(expiredCoupon.isValid()).thenReturn(false);
        when(userCouponRepository.findUserCoupon(userId, couponId))
                .thenReturn(Optional.of(expiredCoupon));

        assertThatThrownBy(() -> couponService.validateCoupon(userId, couponId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("만료되었거나 이미 사용된 쿠폰");
    }

    @Test
    @DisplayName("결제 성공 시 쿠폰 사용 처리")
    void consumeOnPayment_success() {
        Long userId = 1L;
        Long couponId = 100L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCouponRepository.findUserCoupon(userId, couponId)).thenReturn(Optional.of(userCoupon));

        couponService.consumeOnPayment(userId, couponId);

        verify(userCoupon).use();
        verify(userCouponRepository).save(userCoupon);
    }

    @Test
    @DisplayName("결제 실패 시 쿠폰 상태 복구")
    void restoreCouponStatus_success() {
        Long userId = 1L;
        Long couponId = 100L;

        UserCoupon usedCoupon = mock(UserCoupon.class);
        when(usedCoupon.getStatus()).thenReturn(UserCouponStatus.USED);
        when(userCouponRepository.findUserCoupon(userId, couponId))
                .thenReturn(Optional.of(usedCoupon));

        couponService.restoreCouponStatus(userId, couponId);

        verify(usedCoupon).activate();
        verify(userCouponRepository).save(usedCoupon);
    }


}
