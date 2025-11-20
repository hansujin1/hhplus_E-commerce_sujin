package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import com.commerce.hhplus_e_commerce.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyPointTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long testUserId;
    private int initialPoint;

    @BeforeEach
    void setUp() {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getPoint() >= 10000)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("테스트용 사용자가 없습니다."));

        testUserId = user.getUserId();
        initialPoint = user.getPoint();
    }

    @Test
    @DisplayName("동시에 여러 결제 시도 시, 포인트가 음수가 되지 않고 낙관적 락이 동작한다")
    void concurrentPointPayment() throws InterruptedException {
        // Given
        int threadCount = 20;

        List<Order> orders = orderRepository.findAllOrderByUserId(testUserId);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger optimisticLockCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long orderId = orders.get(i).getOrderId();

            executorService.submit(() -> {
                try {
                    paymentService.processPayment(orderId, testUserId);
                    successCount.incrementAndGet();
                } catch (OptimisticLockingFailureException e) {
                    optimisticLockCount.incrementAndGet();
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        User resultUser = userRepository.findByUserId(testUserId)
                .orElseThrow(() -> new IllegalStateException("사용자가 없습니다."));

        System.out.println("초기 포인트: " + initialPoint);
        System.out.println("결제 성공: " + successCount.get());
        System.out.println("결제 실패: " + failCount.get());
        System.out.println("낙관적 락 예외: " + optimisticLockCount.get());
        System.out.println("남은 포인트: " + resultUser.getPoint());

        // 포인트가 음수가 아닌지 확인
        assertThat(resultUser.getPoint()).isGreaterThanOrEqualTo(0);
        // 낙관적 락이 발생했는지 확인
        assertThat(optimisticLockCount.get()).isGreaterThan(0);
    }
}