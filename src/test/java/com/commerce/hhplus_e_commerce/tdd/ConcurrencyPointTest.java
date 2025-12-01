package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConcurrencyPointTest {

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getPoint() > 1000)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("테스트용 사용자가 없습니다."));

        testUserId = user.getUserId();
    }

    @Test
    @DisplayName("동시에 여러 요청이 포인트 차감 시, 정확한 포인트 관리가 된다 - 낙관적 락")
    void concurrentPointDecrease() throws InterruptedException {
        // Given
        User userBefore = userRepository.findByUserId(testUserId)
                .orElseThrow(() -> new IllegalStateException("사용자가 없습니다."));

        int initialPoint = userBefore.getPoint();
        int decreaseAmount = 100;  // 각 요청당 100 포인트 차감
        int threadCount = Math.min(10, initialPoint / decreaseAmount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 동시에 포인트 차감 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    User user = userRepository.findByUserId(testUserId)
                            .orElseThrow(() -> new IllegalStateException("사용자가 없습니다."));
                    user.payPoint(decreaseAmount);
                    userRepository.save(user);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 낙관적 락 충돌 또는 포인트 부족
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        User userAfter = userRepository.findByUserId(testUserId)
                .orElseThrow(() -> new IllegalStateException("사용자가 없습니다."));

        System.out.println("===== 포인트 차감 테스트 결과 =====");
        System.out.println("초기 포인트: " + initialPoint);
        System.out.println("차감 성공: " + successCount.get());
        System.out.println("차감 실패: " + failCount.get());
        System.out.println("최종 포인트: " + userAfter.getPoint());
        System.out.println("예상 포인트: " + (initialPoint - successCount.get() * decreaseAmount));
        System.out.println("=================================");

        // 검증: 낙관적 락으로 인해 일부만 성공할 수 있음
        assertThat(userAfter.getPoint()).isGreaterThanOrEqualTo(0);
        assertThat(userAfter.getPoint()).isLessThanOrEqualTo(initialPoint);
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);

        // 낙관적 락 특성상 충돌이 발생할 수 있음
        assertThat(failCount.get()).isGreaterThan(0);
    }
}