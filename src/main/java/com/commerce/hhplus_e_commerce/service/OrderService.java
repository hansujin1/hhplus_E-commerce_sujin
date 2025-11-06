package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;


    public Order createOrder(@NotBlank Long userId, int totalPrice, int discountPrice, Long couponId) {
        int finalPrice = totalPrice - discountPrice;

        Order order = new Order(
                null,             // PK는 DB에서 생성
                userId,
                totalPrice,
                discountPrice,
                finalPrice,
                0,                // 취소 금액 없음
                new Date(),       // 주문 생성 시간 now
                null,             // 결제 전이므로 paid_dt = null
                couponId
        );

        return orderRepository.save(order);
    }
}
