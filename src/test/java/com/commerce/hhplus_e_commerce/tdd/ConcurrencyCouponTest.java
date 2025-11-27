package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConcurrencyCouponTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    private Long testCouponId;

    @BeforeEach
    void setUp() {
        List<Coupon> coupons = couponRepository.findAllCoupon();
        if (!coupons.isEmpty()) {
            testCouponId = coupons.get(0).getCouponId();
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

        int threadCount = 400;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 400명이 동시에 쿠폰 발급 요청
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;

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
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);

        int actualIssued = couponAfter.getIssuedQuantity() - issuedBefore;
        assertThat(successCount.get()).isEqualTo(actualIssued);
    }
}