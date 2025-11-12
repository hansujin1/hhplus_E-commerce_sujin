package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

@Getter
public class Cart {
    private Long cartID;
    private final Long userId;
    private final Long productId;

    public Cart(Long cartID, Long userId, Long productId) {
        this.cartID = cartID;
        this.userId = userId;
        this.productId = productId;
    }

    public void cartId(Long cartID) {
        this.cartID = cartID;
    }

}
