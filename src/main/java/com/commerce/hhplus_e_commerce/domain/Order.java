package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Order {
    private Long order_id;
    private Long user_id;
    private int total_price;
    private int discount_price;
    private int final_price;
    private int cancelled_price;
    private LocalDate created_dt;
    private LocalDate paid_dt;
    private Long userCouponId;

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
        return this.paid_dt == null; // “아직 결제가 안 된 경우”에만 결제 가능
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
}
