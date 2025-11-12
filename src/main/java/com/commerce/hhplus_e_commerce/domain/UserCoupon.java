package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserCoupon {
    private Long userCouponId;
    private final Long couponId;
    private final Long userId;
    private UserCouponStatus status;
    private final LocalDate issuedDt;
    private LocalDate usedDt;
    private final LocalDate expiresDt;

    public void userCouponId(Long userCouponId) {
        this.userCouponId = userCouponId;
    }

   public UserCoupon(Long userCouponId,Long couponId,Long userId,UserCouponStatus status,LocalDate issuedDt,
                     LocalDate usedDt,LocalDate expiresDt) {
        this.userCouponId = userCouponId;
        this.couponId = couponId;
        this.userId = userId;
        this.status = status;
        this.issuedDt = issuedDt;
        this.usedDt = usedDt;
        this.expiresDt = expiresDt;

   }

   //쿠폰사용 상태 확인
   public boolean isValid() {
       LocalDate now = LocalDate.now();
       boolean notExpired = (expiresDt == null) || !now.isAfter(expiresDt);
       return status == UserCouponStatus.ACTIVE && notExpired;
   }

    public void use() {
        if (!isValid()){
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다");
        }
        this.status = UserCouponStatus.USED;
        this.usedDt = LocalDate.now();
    }

    public void expire() {
        this.status = UserCouponStatus.EXPIRED;
    }

    public void activate() {
        this.status = UserCouponStatus.ACTIVE;
    }

}
