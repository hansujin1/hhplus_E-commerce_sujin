package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 생성 응답(PENDING)")
public record OrderCreateResponse(
        @Schema(description="주문 ID", example="O-1719999999999") String orderId,
        @Schema(description="주문 품목") List<LineItem> items,
        @Schema(description="상품합계", example="1010000") long subtotalAmount,
        @Schema(description="쿠폰 예상 할인금액(정보용)", example="100000") long discountPreview,
        @Schema(description="결제 예정 금액", example="910000") long totalAmount,
        @Schema(description="상태", example="PENDING") String status
){
    public record LineItem(
            String productId, String name, int quantity, long unit_price, long subtotal
    ){}
}
