package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItems {
    private Long order_item_id;
    private Long order_id;
    private Long product_id;
    private String product_name;
    private int product_price;
    private OrderItemStatus status;
    private int quantity;

    public OrderItems() {}

    public OrderItems(Long order_item_id,Long order_id,Long product_id, String product_name, int product_price, OrderItemStatus status, int quantity) {
        this.order_item_id = order_item_id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.status = status;
        this.quantity = quantity;

    }

}
