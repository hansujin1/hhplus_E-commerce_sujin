package com.commerce.hhplus_e_commerce.repository;


import com.commerce.hhplus_e_commerce.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    List<Order> findAllOrderByUserId(Long userId);

    Optional<Order> findByOrderId(Long orderId);

    Optional<Order> findByUserAndOrderId(Long userId, Long orderId);

}
