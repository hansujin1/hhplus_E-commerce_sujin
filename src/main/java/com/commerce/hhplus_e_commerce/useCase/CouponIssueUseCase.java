package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.UserCoupon;
import com.commerce.hhplus_e_commerce.dto.CouponIssueRequest;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 쿠폰 발급 UseCase
 * CouponFacade를 통해 분산 락 적용
 */
@Component
@RequiredArgsConstructor
public class CouponIssueUseCase {
    
    private final CouponFacade couponFacade;

    public CouponIssueResponse issue(Long couponId, CouponIssueRequest req) {
        // 분산 락이 적용된 쿠폰 발급 (CouponFacade 내부)
        UserCoupon issuedCoupon = couponFacade.issueCoupon(req.userId(), couponId);

        return new CouponIssueResponse(
                issuedCoupon.getUserCouponId(),
                issuedCoupon.getCouponId()
        );
    }
}
