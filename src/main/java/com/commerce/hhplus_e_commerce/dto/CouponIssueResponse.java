package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 응답")
public record CouponIssueResponse(
        @Schema(description = "사용자 쿠폰 ID", example="20251030") Long userCouponId
        ,@Schema(description = "쿠폰 ID",example = "202510") Long coupon_id
) {}
