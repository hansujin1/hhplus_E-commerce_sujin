package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCartRepository implements CartRepository{

    private final Map<Long, Cart> cartMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public Cart save(Cart cart) {
        if(cart.getCartID()==null){
            cart.cartId(idGenerator.getAndIncrement());
        }
        cartMap.put(cart.getCartID(), cart);

        return cart;
    }

    @Override
    public List<Cart> findAllCartItems(Long userId) {
        return cartMap.values().stream()
                      .filter(cart -> cart.getCartID().equals(userId))
                      .toList();
    }

    @Override
    public void deleteCartItems(Long userId, Long productId) {
        // UserID , productID가 일치하는 것 담기
        List<Long> removeKeys = cartMap.values().stream()
                                .filter(c -> userId.equals(c.getUserId()) && productId.equals(c.getProductId()))
                                .map(Cart::getCartID)
                                .toList();

        //Map에서 삭제
        removeKeys.forEach(cartMap::remove);
    }
}
