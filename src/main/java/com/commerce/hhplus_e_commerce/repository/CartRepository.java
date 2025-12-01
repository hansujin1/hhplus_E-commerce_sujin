package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Cart;

import java.util.List;

public interface CartRepository {

    Cart save(Cart cart);

    List<Cart> findAllCartItems(Long userId);

    void deleteCartItems(Long userId, Long productId);

    long count();

}
