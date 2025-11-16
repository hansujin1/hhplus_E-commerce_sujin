package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderJpaRepository extends JpaRepository<Order,Long> {

    List<Order> findByUserIdOrderByCreatedDtDesc(Long userId);

    Optional<Order> findByUserIdAndOrderId(Long userId, Long orderId);

}
