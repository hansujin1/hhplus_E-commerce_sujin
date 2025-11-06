package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.dto.CouponIssueRequest;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.service.CouponService;
import org.springframework.stereotype.Component;

@Component
public class CouponIssueUseCase {
    private final CouponService couponService;

    public CouponIssueUseCase(CouponService couponService) {
        this.couponService = couponService;
    }

    public CouponIssueResponse issue(Long couponId ,CouponIssueRequest req) {

        UserCoupon issuedCoupon = couponService.issueCoupon(req.userId(), couponId);

        return new CouponIssueResponse(
                issuedCoupon.getUser_coupon_id(),
                issuedCoupon.getCoupon_id()
        );
    }


}
