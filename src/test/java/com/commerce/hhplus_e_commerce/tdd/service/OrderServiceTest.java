package com.commerce.hhplus_e_commerce.tdd.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("주문 내역 생성 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        Long userId = 1L;
        Long couponId = 2L;

        Order order = new Order(
                                userId
                                ,100_000
                                ,30_000
                                ,70_000
                                ,0
                                ,null
                                ,couponId
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(userId,100_000,30_000,couponId);

        assertThat(result.getFinalPrice()).isEqualTo(order.getFinalPrice());
        assertThat(result.getUserCouponId()).isEqualTo(order.getUserCouponId());
        verify(orderRepository).save(any(Order.class));


    }
}
