package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyCouponTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testCouponId;
    private static final int THREAD_COUNT = 400;

    @BeforeEach
    void setUp() {
        // 쿠폰 조회
        List<Coupon> coupons = couponRepository.findAllCoupon();
        if (!coupons.isEmpty()) {
            testCouponId = coupons.get(0).getCouponId();
        }

        // 테스트용 사용자 생성 (400명)
        long existingUserCount = userRepository.count();
        for (int i = 0; i < THREAD_COUNT; i++) {
            long userId = existingUserCount + i + 1;
            // userId가 이미 존재하는지 확인
            if (userRepository.findByUserId(userId).isEmpty()) {
                userRepository.save(new User("테스트유저" + userId, 100_000));
            }
        }
    }

    @Test
    @DisplayName("동시에 400명이 선착순 쿠폰 발급 요청 시, 정확한 수량만 발급된다 - 분산 락 (Pub/Sub)")
    void concurrentCouponIssue() throws InterruptedException {
        // Given
        Coupon couponBefore = couponRepository.findByCouponId(testCouponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰이 없습니다."));

        int totalQuantity = couponBefore.getTotalQuantity();
        int issuedBefore = couponBefore.getIssuedQuantity();
        int availableQuantity = totalQuantity - issuedBefore;

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 시작 userId 계산 (기존 User 수 + 1)
        long existingUserCount = userRepository.count();
        long startUserId = existingUserCount - THREAD_COUNT + 1;

        // When: 400명이 동시에 쿠폰 발급 요청
        for (int i = 0; i < THREAD_COUNT; i++) {
            long userId = startUserId + i;  // 각자 고유한 userId 사용

            executorService.submit(() -> {
                try {
                    couponFacade.issueCoupon(userId, testCouponId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Coupon couponAfter = couponRepository.findByCouponId(testCouponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰이 없습니다."));

        System.out.println("===== 쿠폰 발급 테스트 결과 =====");
        System.out.println("총 쿠폰 수량: " + totalQuantity);
        System.out.println("발급 전 수량: " + issuedBefore);
        System.out.println("발급 가능 수량: " + availableQuantity);
        System.out.println("발급 성공: " + successCount.get());
        System.out.println("발급 실패: " + failCount.get());
        System.out.println("최종 발급 수량: " + couponAfter.getIssuedQuantity());
        System.out.println("===============================");

        // 검증: 재고가 음수가 아니고, 한도를 초과하지 않았는지
        assertThat(couponAfter.getIssuedQuantity()).isGreaterThanOrEqualTo(issuedBefore);
        assertThat(couponAfter.getIssuedQuantity()).isLessThanOrEqualTo(totalQuantity);
        assertThat(successCount.get()).isGreaterThan(0);
        assertThat(successCount.get() + failCount.get()).isEqualTo(THREAD_COUNT);

        int actualIssued = couponAfter.getIssuedQuantity() - issuedBefore;
        assertThat(successCount.get()).isEqualTo(actualIssued);
    }
}