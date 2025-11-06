package com.commerce.hhplus_e_commerce.controller;

import com.commerce.hhplus_e_commerce.dto.CouponIssueRequest;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.useCase.CouponIssueUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon", description = "선착순 쿠폰 발급 API")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponIssueUseCase couponIssueUseCase;

    //쿠폰 생성 요청
    @Operation(summary = "선착순 쿠폰 발급", description = "잔여 수량 내에서만 발급.")
    @PatchMapping("/{couponId}/issue")
    public CouponIssueResponse issueCoupon(@PathVariable("couponId") Long couponId
                                                          , @RequestBody CouponIssueRequest req){

        return couponIssueUseCase.issue(couponId,req);
    }


}
