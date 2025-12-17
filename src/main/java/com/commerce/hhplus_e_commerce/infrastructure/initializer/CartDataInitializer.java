package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Cart;
import com.commerce.hhplus_e_commerce.repository.CartRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@DependsOn({"userDataInitializer", "productDataInitializer"})
@RequiredArgsConstructor
public class CartDataInitializer {

    private final CartRepository cartRepository;
    private final UserDataInitializer userDataInitializer;

    @PostConstruct
    public void init(){
        if (cartRepository.count() > 0) {
            log.info("Cart 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Long user1Id = userDataInitializer.getUser1Id();
        Long user2Id = userDataInitializer.getUser2Id();

        if (user1Id == null || user2Id == null) {
            log.error("User ID가 초기화되지 않았습니다. Cart 초기화를 건너뜁니다.");
            return;
        }

        log.info("cart Data initializer - 김남준 (userId: {})", user1Id);
        cartRepository.save(new Cart(user1Id, 1L));
        cartRepository.save(new Cart(user1Id, 3L));
        cartRepository.save(new Cart(user1Id, 5L));

        log.info("cart Data initializer - 정호석 (userId: {})", user2Id);
        cartRepository.save(new Cart(user2Id, 2L));
        cartRepository.save(new Cart(user2Id, 3L));
        cartRepository.save(new Cart(user2Id, 4L));
    }
}
