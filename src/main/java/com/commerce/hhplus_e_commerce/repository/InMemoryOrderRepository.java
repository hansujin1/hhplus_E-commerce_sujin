package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository{

    private final Map<Long, Order> orderMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public Order save(Order order) {
        if(order.getOrder_id() == null){
            order.orderId(idGenerator.getAndIncrement());
        } else if (orderMap.containsKey(order.getOrder_id())) {
            throw new IllegalArgumentException("Order already exists with ID: " + order.getOrder_id());
        }

        orderMap.put(order.getOrder_id(), order);

        return order;
    }

    @Override
    public List<Order> findAllOrderByUserId(Long user_id) {

        if (user_id == null) {
            throw new IllegalArgumentException("user_id is null");
        }
        return orderMap.values().stream()
                       .filter(order -> user_id.equals(order.getUser_id()))
                       .toList();
    }

    @Override
    public Optional<Order> findByOrderId(Long order_id) {

        if(order_id == null) {
            throw new IllegalArgumentException("order_id is null");
        }

        return Optional.ofNullable(orderMap.get(order_id));
    }

    @Override
    public Optional<Order> findByUserAndOrderId(Long userId, Long orderId) {
        return orderMap.values().stream()
                .filter(o -> userId.equals(o.getUser_id()) && orderId.equals(o.getOrder_id()))
                .findFirst();
    }

}
