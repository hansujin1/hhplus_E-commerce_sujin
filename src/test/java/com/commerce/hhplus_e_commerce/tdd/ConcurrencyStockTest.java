package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.facade.ProductFacade;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
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
class ConcurrencyStockTest {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRepository productRepository;

    private Long testProductId;

    @BeforeEach
    void setUp() {
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getStock() > 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("테스트용 상품이 없습니다."));

        testProductId = product.getProductId();
    }

    @Test
    @DisplayName("동시에 100명이 같은 상품 재고 차감 시, 정확한 재고 관리가 된다 - 분산 락")
    void concurrentStockDecrease() throws InterruptedException {
        // Given
        Product productBefore = productRepository.selectByProductId(testProductId);
        int initialStock = productBefore.getStock();
        int decreaseQuantity = 1;
        int threadCount = Math.min(100, initialStock);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 동시에 재고 차감 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productFacade.decreaseStockWithLock(testProductId, decreaseQuantity);
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
        Product productAfter = productRepository.selectByProductId(testProductId);

        System.out.println("===== 재고 차감 테스트 결과 =====");
        System.out.println("초기 재고: " + initialStock);
        System.out.println("차감 성공: " + successCount.get());
        System.out.println("차감 실패: " + failCount.get());
        System.out.println("최종 재고: " + productAfter.getStock());
        System.out.println("예상 재고: " + (initialStock - successCount.get()));
        System.out.println("==============================");

        // 검증
        assertThat(productAfter.getStock()).isGreaterThanOrEqualTo(0);
        assertThat(productAfter.getStock()).isEqualTo(initialStock - successCount.get());
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
    }
}