package com.commerce.hhplus_e_commerce.dto;

import com.commerce.hhplus_e_commerce.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 응답")
public record ProductResponse(
        @Schema(description = "상품 ID", example="1") Long productId,
        @Schema(description = "상품명", example="노트북") String productName,
        @Schema(description = "가격", example="80000") int price,
        @Schema(description = "재고", example="10") int stock,
        @Schema(description = "인기도 점수", example="100") int popularityScore
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStock(),
                product.getPopularityScore()
        );
    }
}
