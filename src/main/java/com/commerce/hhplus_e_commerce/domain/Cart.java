package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

@Getter
public class Cart {
    private Long cart_id;
    private final Long user_id;
    private final Long product_id;

    public Cart(Long cart_id, Long user_id, Long product_id) {
        this.cart_id = cart_id;
        this.user_id = user_id;
        this.product_id = product_id;
    }

    public void cartId(Long cart_id) {
        this.cart_id = cart_id;
    }

}
