package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Order {
    private Long orderId;
    private final Long userId;
    private final int totalPrice;
    private final int discountPrice;
    private final int finalPrice;
    private final int cancelledPrice;
    private final LocalDate createdDt;
    private LocalDate paidDt;
    private final Long userCouponId;

    public Order(Long orderId, Long userId, int totalPrice, int discountPrice, int finalPrice,
                 int cancelledPrice, LocalDate createdDt, LocalDate paidDt,Long userCouponId) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
        this.cancelledPrice = cancelledPrice;
        this.createdDt = createdDt;
        this.paidDt = paidDt;
        this.userCouponId = userCouponId;
    }
    /** 결제 가능한 상태인지 확인 */
    public boolean canPay() {
        return this.paidDt == null;
    }

    public void completePayment() {
        if (!canPay()) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }
        this.paidDt = LocalDate.now(); // 결제 시각 기록
    }

    public void cancelPayment() {
        this.paidDt = null; // 결제 기록 제거
    }

    public void orderId(Long orderId) {
        this.orderId = orderId;
    }
}
