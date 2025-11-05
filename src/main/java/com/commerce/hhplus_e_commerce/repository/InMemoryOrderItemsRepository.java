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
        if(orderItems.getOrder_item_id() == null){
            orderItems.setOrder_item_id(idGenerator.getAndIncrement());
        }

        orderItemsMap.put(orderItems.getOrder_item_id(), orderItems);

        return orderItems;
    }

    @Override
    public List<OrderItems> findOrderItemsByOrderId(Long order_id) {

        if (order_id == null) {
            throw new IllegalArgumentException("Order id can't be null");
        }

        return orderItemsMap.values().stream()
                            .filter(item -> order_id.equals(item.getOrder_id()))
                            .toList();
    }

    @Override
    public Optional<OrderItems> findByOrderItemId(Long order_item_id) {

        if(order_item_id == null) {
            throw new IllegalArgumentException("Order id can't be null");
        }

        return Optional.ofNullable(orderItemsMap.get(order_item_id));
    }
}
