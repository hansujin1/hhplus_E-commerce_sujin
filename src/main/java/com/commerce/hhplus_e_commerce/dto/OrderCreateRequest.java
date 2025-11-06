package com.commerce.hhplus_e_commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "주문 생성 요청")
public record OrderCreateRequest( @Schema(description="유저 ID", example="sujin01") @NotBlank Long userId,
                                  @Schema(description="주문 품목") @NotEmpty List<Item> items,
                                  @Schema(description="쿠폰 ID(선택)", example="F-2025Sale") Long couponId) {

    @Schema(description="주문 품목")
    public record Item(
            @Schema(description="상품 ID", example="P001") @NotBlank Long productId,
            @Schema(description="수량", example="2") @Min(1) int quantity
    ){}

}


