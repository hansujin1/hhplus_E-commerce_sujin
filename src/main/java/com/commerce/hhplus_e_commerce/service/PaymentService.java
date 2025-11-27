package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import com.commerce.hhplus_e_commerce.facade.ProductFacade;
import com.commerce.hhplus_e_commerce.repository.OrderItemsRepository;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final UserRepository userRepository;
    private final ProductFacade productFacade;
    private final CouponFacade couponFacade;

    /**
     * 결제 처리
     * User의 @Version으로 낙관적 락 적용
     */
    @Transactional
    public Order processPayment(Long orderId, Long userId) {
        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 결제할 수 있습니다.");
        }
        if (!order.canPay()) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        // 사용자 조회 및 포인트 차감
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        int amount = order.getFinalPrice();
        user.payPoint(amount);
        userRepository.save(user);

        if (order.getUserCouponId() != null) {
            couponFacade.consumeOnPayment(userId, order.getUserCouponId());
        }

        order.completePayment();
        return orderRepository.save(order);
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
