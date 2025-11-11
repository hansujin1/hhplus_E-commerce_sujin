package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Order {
    private Long order_id;
    private final Long user_id;
    private final int total_price;
    private final int discount_price;
    private final int final_price;
    private final int cancelled_price;
    private final LocalDate created_dt;
    private LocalDate paid_dt;
    private final Long userCouponId;

    public Order(Long order_id, Long user_id, int total_price, int discount_price, int final_price,
                 int cancelled_amount, LocalDate created_dt, LocalDate paid_dt,Long userCouponId) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.total_price = total_price;
        this.discount_price = discount_price;
        this.final_price = final_price;
        this.cancelled_price = cancelled_amount;
        this.created_dt = created_dt;
        this.paid_dt = paid_dt;
        this.userCouponId = userCouponId;
    }
    /** 결제 가능한 상태인지 확인 */
    public boolean canPay() {
        return this.paid_dt == null;
    }

    public void completePayment() {
        if (!canPay()) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }
        this.paid_dt = LocalDate.now(); // 결제 시각 기록
    }

    public void cancelPayment() {
        this.paid_dt = null; // 결제 기록 제거
    }

    public void orderId(Long order_id) {
        this.order_id = order_id;
    }
}
