package com.commerce.hhplus_e_commerce.dto;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 생성 응답(PENDING)")
public record OrderCreateResponse(
        @Schema(description="주문 ID", example="O-1719999999999") Long orderId,
        @Schema(description="주문 품목") List<LineItem> items,
        @Schema(description="상품합계", example="1010000") long subtotalAmount,
        @Schema(description="쿠폰 예상 할인금액(정보용)", example="100000") long discountPreview,
        @Schema(description="결제 예정 금액", example="910000") long totalAmount,
        @Schema(description="상태", example="PENDING") OrderItemStatus status
){
    public record LineItem(
            Long productId, String name, int quantity, long unit_price, long subtotal
    ){}

    public static OrderCreateResponse from(Order order, List<Product> products, List<OrderCreateRequest.Item> items) {

        List<LineItem> lineItems = items.stream().map(item -> {
            Product product = products.stream()
                    .filter(p -> p.getProductId().equals(item.productId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("상품 정보 없음"));

            long subtotal = product.calculatePrice(item.quantity());

            return new LineItem(
                    product.getProductId(),
                    product.getProductName(),
                    item.quantity(),
                    product.getPrice(),
                    subtotal
            );
        }).toList();

        return new OrderCreateResponse(
                order.getOrderId(),
                lineItems,
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getFinalPrice(),
                OrderItemStatus.PENDING
        );
    }
}
