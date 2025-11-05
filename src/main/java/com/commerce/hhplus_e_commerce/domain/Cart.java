package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart {
    private Long cart_id;
    private Long user_id;
    private Long product_id;

    public Cart() {}

    public Cart(Long cart_id, Long user_id, Long product_id) {
        this.cart_id = cart_id;
        this.user_id = user_id;
        this.product_id = product_id;
    }

}
