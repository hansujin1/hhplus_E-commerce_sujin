package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartJpaRepository extends JpaRepository<Cart,Long> {

    List<Cart> findByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

}
