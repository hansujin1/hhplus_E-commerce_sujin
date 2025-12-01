package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.repository.OrderItemsRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.OrderItemsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderItemsRepositoryImpl implements OrderItemsRepository {
    private final OrderItemsJpaRepository  orderItemsJpaRepository;


    @Override
    public OrderItems save(OrderItems orderItems) {
        return orderItemsJpaRepository.save(orderItems);
    }

    @Override
    public List<OrderItems> findOrderItemsByOrderId(Long orderId) {
        return orderItemsJpaRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<OrderItems> findByOrderItemIdAndOrderID(Long orderItemId,Long orderId) {
        return orderItemsJpaRepository.findByOrderItemIdAndOrderId(orderItemId,orderId);
    }

    @Override
    public List<OrderItems> findAll() {
        return orderItemsJpaRepository.findAll();
    }

    @Override
    public long count() {
        return orderItemsJpaRepository.count();
    }
}
