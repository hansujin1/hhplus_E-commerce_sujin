package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.Cart;
import com.commerce.hhplus_e_commerce.repository.CartRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart save(Cart cart) {
        return cartJpaRepository.save(cart);
    }

    @Override
    public List<Cart> findAllCartItems(Long userId) {
        return cartJpaRepository.findByUserId(userId);
    }

    @Override
    public void deleteCartItems(Long userId, Long productId) {
        cartJpaRepository.deleteByUserIdAndProductId(userId,productId);
    }
}
