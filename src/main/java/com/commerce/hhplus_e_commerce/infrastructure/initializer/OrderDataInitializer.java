package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Slf4j
@Component
@DependsOn({"userDataInitializer"})
public class OrderDataInitializer {

    private final OrderRepository orderRepository;

    public OrderDataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init(){
        if (orderRepository.count() > 0) {
            log.info("Order 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("order 정보 초기 셋팅하기");
        // userId 1L, 2L로 수정
        orderRepository.save(new Order(1L, 80_000, 0, 80_000, 0, LocalDate.now(), null));
        // 15% 세일
        orderRepository.save(new Order(1L, 85_000, 12_750, 72_250, 0, LocalDate.now(), 2L));
    }



}
