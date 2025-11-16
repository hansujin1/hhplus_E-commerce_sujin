package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Slf4j
@Component
public class OrderDataInitializer {

    private final OrderRepository orderRepository;

    public OrderDataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init(){
        log.info("order 정보 초기 셋팅하기");
        //1L,2L
        orderRepository.save(new Order(20250202L,80_000,0,80_000,0, LocalDate.now(),null));
        //3L,5L,6L --> 15% 세일
        orderRepository.save(new Order(20250202L,85_000,12_750,72_250,0,LocalDate.now(),2L));
    }



}
