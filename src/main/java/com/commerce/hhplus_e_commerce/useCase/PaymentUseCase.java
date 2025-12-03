package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.dto.PaymentRequest;
import com.commerce.hhplus_e_commerce.dto.PaymentResponse;
import com.commerce.hhplus_e_commerce.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * 결제 UseCase
 * PaymentFacade를 통해 결제 처리 (낙관적 락 사용)
 */
@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    
    private final PaymentFacade paymentFacade;

    @Retryable(maxAttempts = 3,backoff = @Backoff(delay = 100, multiplier = 2),value = ObjectOptimisticLockingFailureException.class)
    public PaymentResponse payOrder(Long orderId, PaymentRequest req) {
        try {
            // 포인트 결제 (User 엔티티의 @Version으로 낙관적 락 적용)
            Order order = paymentFacade.processPayment(orderId, req.userId());
            return PaymentResponse.success(order.getOrderId(), order.getFinalPrice());

        } catch (Exception e) {
            // 실패 → 보상 (재고/쿠폰 원복)
            paymentFacade.rollbackPayment(orderId, req.userId());
            return PaymentResponse.fail(orderId, e.getMessage());
        }
    }
}
