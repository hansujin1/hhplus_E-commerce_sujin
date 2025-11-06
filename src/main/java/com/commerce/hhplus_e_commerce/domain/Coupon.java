package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.CouponStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Coupon {
    private Long coupon_id;
    private String coupon_name;
    private double discount_rate;
    private String discount_type;
    private int total_quantity;
    private int issued_amount; // 발급수량
    private Date start_date;
    private Date end_date;
    private int valid_days; //사용가능 일자
    private CouponStatus status;

    public Coupon() {}

    public Coupon(Long coupon_id,String coupon_name,double discount_rate,String discount_type,int total_quantity,
                  int issued_amount, Date start_date, Date end_date, int valid_days, CouponStatus status){
        this.coupon_id = coupon_id;
        this.coupon_name = coupon_name;
        this.discount_rate = discount_rate;
        this.discount_type = discount_type;
        this.total_quantity = total_quantity;
        this.issued_amount = issued_amount;
        this.start_date = start_date;
        this.end_date = end_date;
        this.valid_days = valid_days;
        this.status = status;
    }

    /** 발급 가능 기간인지 검증 */
    public boolean isWithinPeriod() {
        Date now = new Date();
        return (start_date == null || !now.before(start_date)) &&
                (end_date == null || !now.after(end_date));
    }

    /** 발급 가능 여부 확인 */
    public boolean canIssue() {
        return status == CouponStatus.ISSUING &&
                issued_amount < total_quantity &&
                isWithinPeriod();
    }

    /** 발급 시 수량 증가 */
    public void issue() {
        if (!canIssue()) {
            throw new IllegalStateException("쿠폰을 더 이상 발급할 수 없습니다.");
        }
        this.issued_amount++;
    }

   /** 할인가 계산 */
    public int calculateDiscount(int totalPrice) {
        if ("RATE".equalsIgnoreCase(discount_type)) {
            return (int) Math.floor(totalPrice * discount_rate);
        }
        if ("FIXED".equalsIgnoreCase(discount_type)) {
            return (int) discount_rate;
        }
        throw new IllegalStateException("지원하지 않는 할인 타입: " + discount_type);
    }

}
