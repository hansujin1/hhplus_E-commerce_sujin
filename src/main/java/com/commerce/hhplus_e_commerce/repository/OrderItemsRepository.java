package com.commerce.hhplus_e_commerce.repository;


import com.commerce.hhplus_e_commerce.domain.OrderItems;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepository {

    OrderItems save(OrderItems orderItems);

    List<OrderItems> findOrderItemsByOrderId(Long orderId);

    Optional<OrderItems> findByOrderItemIdAndOrderID(Long orderItemId,Long orderId);

    List<OrderItems> findAll();

    long count();

}
