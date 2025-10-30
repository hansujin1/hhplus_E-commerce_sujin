package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 응답")
public record PaymentResponse(
        @Schema(description="주문 ID", example="O-1719999999999") String orderId,
        @Schema(description="결제 금액(차감된 포인트)", example="910000") long paidAmount,
        @Schema(description="남은 포인트", example="90000") long remainingBalance,
        @Schema(description="상태", example="SUCCESS") String status,
        @Schema(description="외부 전송(목킹)", example="QUEUED") String dataTransmission
){}