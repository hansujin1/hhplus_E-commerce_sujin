package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.OrderItems;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderItemsRepository implements OrderItemsRepository{

    private final Map<Long, OrderItems> orderItemsMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public OrderItems save(OrderItems orderItems) {
        if(orderItems.getOrderItemId() == null){
            orderItems.orderItemId(idGenerator.getAndIncrement());
        }

        orderItemsMap.put(orderItems.getOrderItemId(), orderItems);

        return orderItems;
    }

    @Override
    public List<OrderItems> findOrderItemsByOrderId(Long orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order id can't be null");
        }

        return orderItemsMap.values().stream()
                            .filter(item -> orderId.equals(item.getOrderId()))
                            .toList();
    }

    @Override
    public Optional<OrderItems> findByOrderItemId(Long orderItemId) {

        if(orderItemId == null) {
            throw new IllegalArgumentException("Order id can't be null");
        }

        return Optional.ofNullable(orderItemsMap.get(orderItemId));
    }
}
