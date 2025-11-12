package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserCoupon {
    private Long user_coupon_id;
    private final Long coupon_id;
    private final Long user_id;
    private UserCouponStatus status;
    private final LocalDate issued_date;
    private LocalDate used_date;
    private final LocalDate expires_date;

    public void userCouponId(Long user_coupon_id) {
        this.user_coupon_id = user_coupon_id;
    }

   public UserCoupon(Long user_coupon_id,Long coupon_id,Long user_id,UserCouponStatus status,LocalDate issued_date,
                     LocalDate used_date,LocalDate expires_date) {
        this.user_coupon_id = user_coupon_id;
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.status = status;
        this.issued_date = issued_date;
        this.used_date = used_date;
        this.expires_date = expires_date;

   }

   //쿠폰사용 상태 확인
   public boolean isValid() {
       LocalDate now = LocalDate.now();
       boolean notExpired = (expires_date == null) || !now.isAfter(expires_date);
       return status == UserCouponStatus.ACTIVE && notExpired;
   }

    public void use() {
        if (!isValid()){
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다");
        }
        this.status = UserCouponStatus.USED;
        this.used_date = LocalDate.now();
    }

    public void expire() {
        this.status = UserCouponStatus.EXPIRED;
    }

    public void activate() {
        this.status = UserCouponStatus.ACTIVE;
    }

}
