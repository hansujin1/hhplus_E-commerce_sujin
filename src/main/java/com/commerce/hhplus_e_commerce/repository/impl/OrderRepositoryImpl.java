package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public List<Order> findAllOrderByUserId(Long userId) {
        return orderJpaRepository.findByUserIdOrderByCreatedDtDesc(userId);
    }

    @Override
    public Optional<Order> findByOrderId(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<Order> findByUserAndOrderId(Long userId, Long orderId) {
        return orderJpaRepository.findByUserIdAndOrderId(userId,orderId);
    }

    @Override
    public long count() {
        return orderJpaRepository.count();
    }
}
