package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderItemsJpaRepository extends JpaRepository<OrderItems,Long> {

    List<OrderItems> findByOrderId(Long orderId);

    Optional<OrderItems> findByOrderItemIdAndOrderId(Long orderItemId,Long orderId);

}
