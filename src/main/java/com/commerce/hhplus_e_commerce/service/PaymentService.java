package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.event.OrderCompletedEvent;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import com.commerce.hhplus_e_commerce.facade.ProductFacade;
import com.commerce.hhplus_e_commerce.infrastructure.kafka.KafkaProducerService;
import com.commerce.hhplus_e_commerce.repository.OrderItemsRepository;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final UserRepository userRepository;
    private final ProductFacade productFacade;
    private final CouponFacade couponFacade;
    private final KafkaProducerService kafkaProducerService;

    /**
     * 결제 처리
     * User의 @Version으로 낙관적 락 적용
     */
    @Transactional
    public Order processPayment(Long orderId, Long userId) {
        System.out.println("===== processPayment 시작 =====");
        System.out.println("orderId: " + orderId + ", userId: " + userId);
        
        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다."));

        System.out.println("주문 조회 완료 - userCouponId: " + order.getUserCouponId());

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 결제할 수 있습니다.");
        }
        if (!order.canPay()) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        // 사용자 조회 및 포인트 차감
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        System.out.println("유저 조회 완료 - 현재 포인트: " + user.getPoint() + ", version: " + user.getVersion());

        int amount = order.getFinalPrice();
        user.payPoint(amount);
        userRepository.save(user);

        System.out.println("포인트 차감 완료 - 남은 포인트: " + user.getPoint());

        if (order.getUserCouponId() != null) {
            System.out.println("쿠폰 사용 처리 시작");
            couponFacade.consumeOnPayment(userId, order.getUserCouponId());
        } else {
            System.out.println("쿠폰 없음 - 쿠폰 처리 스킵");
        }

        order.completePayment();
        Order saved = orderRepository.save(order);
        
        // 트랜잭션 커밋 후 Kafka 이벤트 발행
        registerAfterCommitEvent(saved);
        
        System.out.println("===== processPayment 완료 =====");
        return saved;
    }

    /**
     * 트랜잭션 커밋 후 주문 완료 이벤트 발행
     */
    private void registerAfterCommitEvent(Order order) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    OrderCompletedEvent event = OrderCompletedEvent.builder()
                            .orderId(order.getOrderId())
                            .userId(order.getUserId())
                            .totalAmount(order.getFinalPrice())
                            .orderDate(LocalDateTime.now())
                            .orderStatus(order.getPaidDt() != null ? "COMPLETED" : "PENDING")
                            .build();
                    
                    kafkaProducerService.publishOrderCompletedEvent(event);
                    System.out.println("주문 완료 이벤트 발행 완료: orderId=" + order.getOrderId());
                }
            }
        );
    }

    /**
     * 결제 실패 시 보상 처리 (원복)
     */
    @Transactional
    public void rollbackPayment(Long orderId, Long userId) {
        List<OrderItems> orderItems = orderItemsRepository.findOrderItemsByOrderId(orderId);
        productFacade.restoreStockByOrderItems(orderItems);

        if (userId != null) {
            couponFacade.restoreCouponStatus(userId, orderId);
        }
    }
}
