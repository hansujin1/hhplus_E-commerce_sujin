package com.commerce.hhplus_e_commerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "user_id", nullable = false)
    private  Long userId;
    @Column(name = "total_price", nullable = false)
    private  int totalPrice;
    @Column(name = "discount_price", nullable = false)
    private  int discountPrice;
    @Column(name = "final_price")
    private  int finalPrice;
    @Column(name = "cancelled_price")
    private  int cancelledPrice;
    @Column(name = "created_dt", nullable = false)
    private  LocalDate createdDt;
    @Column(name = "paid_dt")
    private LocalDate paidDt;
    @Column(name = "user_coupon_id")
    private  Long userCouponId;

    public Order(Long userId, int totalPrice, int discountPrice, int finalPrice,
                 int cancelledPrice, LocalDate paidDt,Long userCouponId) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
        this.cancelledPrice = cancelledPrice;
        this.createdDt = LocalDate.now();
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
