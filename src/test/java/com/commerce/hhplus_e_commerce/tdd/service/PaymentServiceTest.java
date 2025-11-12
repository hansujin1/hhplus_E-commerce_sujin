package com.commerce.hhplus_e_commerce.tdd.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import com.commerce.hhplus_e_commerce.service.CouponService;
import com.commerce.hhplus_e_commerce.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("지불하는 로직 테스트")
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private PaymentService paymentService;

    private Long userId;
    private Long couponId;
    private Long orderId;
    private LocalDate createdDt;

    @BeforeEach
    void setUp() {
        userId = 1L;
        couponId = 2L;
        orderId = 1L;
        createdDt = LocalDate.now();
    }


    @Test
    @DisplayName("결제하는 로직 테스트")
    void processPayment()  {


        Order order = new Order(
                orderId
                ,userId
                ,100_000
                ,30_000
                ,70_000
                ,0
                ,createdDt
                ,null
                ,couponId
        );
        User user = new User(
                userId
                ,"김남준"
                ,100_000
                ,createdDt
        );

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(couponService).consumeOnPayment(userId, couponId);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = paymentService.processPayment(orderId, userId);

        assertThat(result).isNotNull();

        InOrder inOrder = inOrder(userRepository, couponService, orderRepository);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(couponService).consumeOnPayment(userId, couponId);
        inOrder.verify(orderRepository).save(order);

    }

    @DisplayName("point가 부족하여 지불 실패")
    @Test
    void processPaymentFail()  {
        Order order = new Order(
                orderId
                ,userId
                ,100_000
                ,30_000
                ,70_000
                ,0
                ,createdDt
                ,null
                ,couponId
        );
        User user = new User(
                userId
                ,"김남준"
                ,50_000
                ,createdDt
        );
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> paymentService.processPayment(orderId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포인트 부족");

        assertThat(order.getPaidDt()).isNull();

        assertThat(user.getPoint()).isEqualTo(50_000);

        verify(userRepository, never()).save(any());
        verify(couponService, never()).consumeOnPayment(any(), any());
        verify(orderRepository, never()).save(any());

    }
}
