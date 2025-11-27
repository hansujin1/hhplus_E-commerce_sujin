package com.commerce.hhplus_e_commerce.facade;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;

    public Order processPayment(Long orderId, Long userId) {
        return paymentService.processPayment(orderId, userId);
    }

    public void rollbackPayment(Long orderId, Long userId) {
        paymentService.rollbackPayment(orderId, userId);
    }
}