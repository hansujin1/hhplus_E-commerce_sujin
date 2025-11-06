package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "결제 요청(포인트 + 쿠폰)")
public record PaymentRequest(
        @Schema(description="유저 ID", example="sujin01") @NotBlank Long userId
){}
