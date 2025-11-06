package com.commerce.hhplus_e_commerce.dto;

import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 응답")
public record PaymentResponse(
        @Schema(description="주문 ID", example="O-1719999999999") Long orderId,
        @Schema(description="결제 금액(차감된 포인트)", example="910000") int paidAmount,
        @Schema(description="상태", example="SUCCESS") OrderItemStatus status,
        @Schema(description="외부 전송(목킹)", example="QUEUED") String dataTransmission
){

    public static PaymentResponse success(Long orderId, int paidAmount) {
        return new PaymentResponse(
                orderId,
                paidAmount,
                OrderItemStatus.PAID,
                "QUEUED"
        );
        }
        public static PaymentResponse fail(Long orderId, String message) {
            return new PaymentResponse(
                    orderId,
                    0,
                    OrderItemStatus.FAILED,
                    message   // "QUEUED" 대신 실패 사유를 넣음
            );
    }
}