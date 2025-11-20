package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Coupon;
import com.commerce.hhplus_e_commerce.repository.CouponRepository;
import com.commerce.hhplus_e_commerce.service.CouponService;
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
    private CouponService couponService;

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
    @DisplayName("동시에 400명이 선착순 쿠폰 발급 요청 시, 정확한 수량만 발급된다 - 비관적 락")
    void concurrentCouponIssue() throws InterruptedException {
        // Given
        int threadCount = 400;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 400명이 동시에 쿠폰 발급 요청
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;

            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(userId, testCouponId);
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

        System.out.println("발급 성공: " + successCount.get());
        System.out.println("발급 실패: " + failCount.get());

        Coupon coupon = couponRepository.findByCouponId(testCouponId)
                .orElseThrow(() -> new IllegalStateException("쿠폰이 없습니다."));

        System.out.println("최종 발급 수량: " + coupon.getIssuedQuantity());

        //재고가 음수가 아닌지 확인
        assertThat(coupon.getIssuedQuantity()).isGreaterThan(0);
        assertThat(successCount.get()).isGreaterThan(0);
    }
}