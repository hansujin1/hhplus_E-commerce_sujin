package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.dto.PaymentRequest;
import com.commerce.hhplus_e_commerce.dto.PaymentResponse;
import com.commerce.hhplus_e_commerce.service.PaymentService;

public class PaymentUseCase {
    private final PaymentService paymentService;

    public PaymentUseCase(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResponse payOrder(Long orderId, PaymentRequest req) {

        try {
            Order order = paymentService.processPayment(orderId, req.userId());
            return PaymentResponse.success(order.getOrderId(), order.getFinalPrice());

        } catch (Exception e) {
            // 실패 → 보상 (재고/쿠폰 원복)
            paymentService.rollbackPayment(orderId, req.userId());
            return PaymentResponse.fail(orderId, e.getMessage());
        }

    }
}
