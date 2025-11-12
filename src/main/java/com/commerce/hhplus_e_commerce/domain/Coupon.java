package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import com.commerce.hhplus_e_commerce.domain.enums.DiscountType;
import lombok.Getter;

import java.util.Date;

@Getter
public class Coupon {
    private Long couponId;
    private final String couponName;
    private final double discountRate;
    private final DiscountType discountType;
    private final int totalQuantity;
    private int issuedAmount; // 발급수량
    private final Date startDate;
    private final Date endDate;
    private final int validDays; //사용가능 일자
    private final CouponStatus status;

    public Coupon(Long couponId,String couponName,double discountRate,DiscountType discountType,int totalQuantity,
                  int issuedAmount, Date startDate, Date endDate, int validDays, CouponStatus status){
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.discountType = discountType;
        this.totalQuantity = totalQuantity;
        this.issuedAmount = issuedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validDays = validDays;
        this.status = status;
    }

    public void couponId(Long couponId){
        this.couponId = couponId;
    }

    /** 발급 가능 기간인지 검증 */
    public boolean isWithinPeriod() {
        Date now = new Date();
        return (startDate == null || !now.before(startDate)) &&
                (endDate == null || !now.after(endDate));
    }

    /** 발급 가능 여부 확인 */
    public boolean canIssue() {
        return status == CouponStatus.ISSUING &&
                issuedAmount < totalQuantity &&
                isWithinPeriod();
    }

    /** 발급 시 수량 증가 */
    public void issue() {
        if (!canIssue()) {
            throw new IllegalStateException("쿠폰을 더 이상 발급할 수 없습니다.");
        }
        this.issuedAmount++;
    }

   /** 할인가 계산 */
    public int calculateDiscount(int totalPrice) {
        if(DiscountType.FIXED.equals(discountType)) {
            return (int) discountRate;
        }
        if(DiscountType.RATE.equals(discountType)) {
            return (int) Math.floor(totalPrice * discountRate);
        }
        throw new IllegalStateException("지원하지 않는 할인 타입: " + discountType);
    }

}
