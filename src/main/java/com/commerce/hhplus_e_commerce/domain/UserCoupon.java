package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class UserCoupon {
    private Long user_coupon_id;
    private Long coupon_id;
    private Long user_id;
    private UserCouponStatus status;
    private Date issued_date;
    private Date used_date;
    private Date expires_date;

    public UserCoupon() {}

   public UserCoupon(Long user_coupon_id,Long coupon_id,Long user_id,UserCouponStatus status,Date issued_date,
                     Date used_date,Date expires_date) {
        this.user_coupon_id = user_coupon_id;
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.status = status;
        this.issued_date = issued_date;
        this.used_date = used_date;
        this.expires_date = expires_date;

   }

}
