package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Cart;
import com.commerce.hhplus_e_commerce.repository.CartRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CartDataInitializer {

    private final CartRepository cartRepository;

    public CartDataInitializer(CartRepository productRepository) {
        this.cartRepository = productRepository;
    }

    @PostConstruct
    public void init(){
        log.info("cart Data initializer - 김남준");
        cartRepository.save(new Cart(20250202L,1L));
        cartRepository.save(new Cart(20250202L,3L));
        cartRepository.save(new Cart(20250202L,5L));

        log.info("cart Data initializer - 정호석");
        cartRepository.save(new Cart(20250222L,2L));
        cartRepository.save(new Cart(20250222L,3L));
        cartRepository.save(new Cart(20250222L,4L));
    }
}
