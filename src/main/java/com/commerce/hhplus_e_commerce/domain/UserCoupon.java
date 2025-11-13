package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.UserCouponStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long userCouponId;
    @Column(name = "coupon_id", nullable = false)
    private  Long couponId;
    @Column(name = "user_id", nullable = false)
    private  Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserCouponStatus status;
    @Column(name = "issued_dt", nullable = false)
    private  LocalDate issuedDt;
    @Column(name = "used_dt")
    private LocalDate usedDt;
    @Column(name = "expires_dt", nullable = false)
    private  LocalDate expiresDt;

    public void userCouponId(Long userCouponId) {
        this.userCouponId = userCouponId;
    }

   public UserCoupon(Long userCouponId,Long couponId,Long userId,UserCouponStatus status
                     ,LocalDate expiresDt) {
        this.userCouponId = userCouponId;
        this.couponId = couponId;
        this.userId = userId;
        this.status = status;
        this.issuedDt = LocalDate.now();
        this.usedDt = null;
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
