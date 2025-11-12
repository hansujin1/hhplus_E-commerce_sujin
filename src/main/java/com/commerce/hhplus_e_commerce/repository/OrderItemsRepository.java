package com.commerce.hhplus_e_commerce.repository;


import com.commerce.hhplus_e_commerce.domain.OrderItems;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepository {

    OrderItems save(OrderItems orderItems);

    List<OrderItems> findOrderItemsByOrderId(Long order_id);

    Optional<OrderItems> findByOrderItemId(Long order_item_id);

}
