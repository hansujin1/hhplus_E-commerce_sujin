package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 응답")
public record CouponIssueResponse(
        @Schema(description = "쿠폰 ID", example="UC-20251030") String userCouponId
        ,@Schema(description = "쿠폰명", example = "가을할인프로모션") Object coupon_name
        ,@Schema(description = "할인", example = "10") int discount_rate
        ,@Schema(description = "할인타입", example = "PERCENT")String type
        , @Schema(description = "만료일시", example = "2025-12-31T23:59:59Z") String expiresAt
        ,@Schema(description = "남은 수량",example="95")  int remainingQuantity
) {}
