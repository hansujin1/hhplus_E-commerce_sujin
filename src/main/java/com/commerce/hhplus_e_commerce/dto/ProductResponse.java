package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 응답")
public record ProductResponse(@Schema(description = "상품 ID", example="P001") String productId,
                              @Schema(description = "상품명", example="노트북") String product_name,
                              @Schema(description = "가격", example="80000") long price,
                              @Schema(description = "재고", example="10") int stock) {
}
