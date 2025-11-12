package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryCartRepository implements CartRepository{

    private final Map<Long, Cart> cartMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public Cart save(Cart cart) {
        if(cart.getCart_id()==null){
            cart.cartId(idGenerator.getAndIncrement());
        }
        cartMap.put(cart.getCart_id(), cart);

        return cart;
    }

    @Override
    public List<Cart> findAllCartItems(Long userId) {
        return cartMap.values().stream()
                      .filter(cart -> cart.getCart_id().equals(userId))
                      .collect(Collectors.toList());
    }

    @Override
    public void deleteCartItems(Long userId, Long productId) {
        // UserID , productID가 일치하는 것 담기
        List<Long> removeKeys = cartMap.values().stream()
                                .filter(c -> userId.equals(c.getUser_id()) && productId.equals(c.getProduct_id()))
                                .map(Cart::getCart_id)
                                .toList();

        //Map에서 삭제
        removeKeys.forEach(cartMap::remove);
    }
}
