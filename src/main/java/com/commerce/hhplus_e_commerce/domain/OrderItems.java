package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import lombok.Getter;

@Getter
public class OrderItems {
    private Long orderItemId;
    private final Long orderId;
    private final Long productId;
    private final String productName;
    private final int productPrice;
    private final OrderItemStatus status;
    private final int quantity;

    public OrderItems(Long orderItemId,Long orderId,Long productId, String productName,
                      int productPrice, OrderItemStatus status, int quantity) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.status = status;
        this.quantity = quantity;

    }

    public void orderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

}
