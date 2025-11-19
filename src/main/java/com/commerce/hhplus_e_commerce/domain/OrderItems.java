package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;
    @Column(name = "order_id", nullable = false)
    private  Long orderId;
    @Column(name = "product_id", nullable = false)
    private  Long productId;
    @Column(name = "product_name", nullable = false)
    private  String productName;
    @Column(name = "product_price", nullable = false)
    private  int productPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private  OrderItemStatus status;
    @Column(name = "quantity", nullable = false)
    private  int quantity;

    public OrderItems(Long orderId,Long productId, String productName,
                      int productPrice, OrderItemStatus status, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.status = status;
        this.quantity = quantity;

    }


}
