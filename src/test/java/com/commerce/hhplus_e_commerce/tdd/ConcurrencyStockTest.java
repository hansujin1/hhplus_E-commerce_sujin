package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import com.commerce.hhplus_e_commerce.service.ProductService;
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
class ConcurrencyStockTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long testProductId;
    private int initialStock;

    @BeforeEach
    void setUp() {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            testProductId = product.getProductId();
            initialStock = product.getStock();
        }
    }

    @Test
    @DisplayName("동시에 500명이 같은 상품 1개씩 주문 시, 재고가 음수가 되지 않는다 - 비관적 락")
    void concurrentStockDecrease() throws InterruptedException {
        int threadCount = 500;
        int orderQuantity = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 500명이 동시에 1개씩 주문
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderCreateRequest.Item item = new OrderCreateRequest.Item(testProductId, orderQuantity);
                    productService.minusStock(List.of(item));
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

        Product resultProduct = productRepository.selectByProductId(testProductId);

        System.out.println("초기 재고: " + initialStock);
        System.out.println("주문 성공: " + successCount.get());
        System.out.println("주문 실패: " + failCount.get());
        System.out.println("남은 재고: " + resultProduct.getStock());

        // 재고가 음수가 아닌지 확인
        assertThat(resultProduct.getStock()).isGreaterThanOrEqualTo(0);
        // 초기 재고 - 성공 수 = 남은 재고
        assertThat(resultProduct.getStock()).isEqualTo(initialStock - successCount.get());
    }
}
