package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import com.commerce.hhplus_e_commerce.domain.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;
    @Column(name = "coupon_name", nullable = false)
    private  String couponName;
    @Column(name = "discount_rate", nullable = false)
    private  double discountRate;
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private  DiscountType discountType;
    @Column(name = "total_quantity", nullable = false)
    private  int totalQuantity;
    @Column(name = "issued_quantity")
    private int issuedQuantity; // 발급수량
    @Column(name = "start_date")
    private  LocalDate startDate;
    @Column(name = "end_date")
    private  LocalDate endDate;
    @Column(name = "valid_days", nullable = false)
    private  int validDays; //사용가능 일자
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private  CouponStatus status;

    public Coupon( String couponName, double discountRate, DiscountType discountType, int totalQuantity,
                  int issuedQuantity, LocalDate startDate, LocalDate endDate, int validDays, CouponStatus status){
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.discountType = discountType;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validDays = validDays;
        this.status = status;
    }

    public Coupon(Long couponId, String couponName, double discountRate, DiscountType discountType, int totalQuantity,
                   int issuedQuantity, LocalDate startDate, LocalDate endDate, int validDays, CouponStatus status){
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.discountType = discountType;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validDays = validDays;
        this.status = status;
    }

    /** 발급 가능 기간인지 검증 */
    public boolean isWithinPeriod() {
        LocalDate now = LocalDate.now();
        return (startDate == null || !now.isBefore(startDate)) &&
                (endDate == null || !now.isAfter(endDate));
    }

    /** 발급 가능 여부 확인 */
    public boolean canIssue() {
        return status == CouponStatus.ISSUING &&
                issuedQuantity < totalQuantity &&
                isWithinPeriod();
    }

    /** 발급 시 수량 증가 */
    public void issue() {
        if (!canIssue()) {
            throw new IllegalStateException("쿠폰을 더 이상 발급할 수 없습니다.");
        }
        this.issuedQuantity++;
    }

   /** 할인가 계산 */
    public int calculateDiscount(int totalPrice) {
        if(DiscountType.FIXED.equals(discountType)) {
            return (int) Math.floor(totalPrice - discountRate);
        }
        if(DiscountType.RATE.equals(discountType)) {
            return (int) Math.floor(totalPrice * discountRate);
        }
        throw new IllegalStateException("지원하지 않는 할인 타입: " + discountType);
    }

}
