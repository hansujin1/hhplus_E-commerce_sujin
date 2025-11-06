package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.OrderItemsRepository;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CouponService couponService;

    public PaymentService(OrderRepository orderRepository, OrderItemsRepository orderItemsRepository, UserRepository userRepository, ProductService productService, CouponService couponService) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.couponService = couponService;
    }

    /**
     * 실제 비즈니스 로직은 service에 위치
     */
    public Order processPayment(Long orderId, Long userId) {

        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다."));

        if (!order.getUser_id().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 결제할 수 있습니다.");
        }
        if (!order.canPay()) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        // 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        int amount = order.getFinal_price();

        // 포인트 차감 (여기서 실패 가능)
        if (user.getPoint() < amount) {
            throw new IllegalStateException("포인트 잔액이 부족합니다.");
        }
        user.setPoint(user.getPoint() - amount);
        userRepository.save(user);

        if (order.getUserCouponId() != null) {
            couponService.consumeOnPayment(userId, order.getUserCouponId());
        }

        // 결제 완료 처리
        order.completePayment();
        return orderRepository.save(order);
    }

    /**
     * 실패 시 보상 처리 (원복)
     */
    public void rollbackPayment(Long orderId, Long userId) {

        List<OrderItems> orderItems = orderItemsRepository.findOrderItemsByOrderId(orderId);
        productService.restoreStockByOrderItems(orderItems);

        if (userId != null) {
            couponService.restoreCouponStatus(userId, orderId);
        }
    }
}
