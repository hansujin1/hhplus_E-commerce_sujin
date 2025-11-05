package com.commerce.hhplus_e_commerce.repository;


import com.commerce.hhplus_e_commerce.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    List<Order> findAllOrderByUserId(Long user_id);

    Optional<Order> findByOrderId(Long order_id);

    Optional<Order> findByUserAndOrderId(Long user_id, Long order_id);
}
