package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 요청")
public record CouponIssueRequest(
        @Schema(description="사용자 ID", example="sujin") Long userId
) {
}
