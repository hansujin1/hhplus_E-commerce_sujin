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

    @Retryable(
        value = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public PaymentResponse payOrder(Long orderId, PaymentRequest req) {
        try {
            // 포인트 결제 (User 엔티티의 @Version으로 낙관적 락 적용)
            Order order = paymentFacade.processPayment(orderId, req.userId());
            return PaymentResponse.success(order.getOrderId(), order.getFinalPrice());

        } catch (ObjectOptimisticLockingFailureException e) {
            // 낙관적 락 충돌 → @Retryable이 재시도하도록 예외를 다시 던짐
            System.out.println("낙관적 락 충돌 발생! @Retryable이 재시도합니다.");
            throw e;
            
        } catch (Exception e) {
            // 다른 예외 → 보상 처리
            System.out.println("결제 실패 (재시도 불가): " + e.getMessage());
            paymentFacade.rollbackPayment(orderId, req.userId());
            return PaymentResponse.fail(orderId, e.getMessage());
        }
    }
}
