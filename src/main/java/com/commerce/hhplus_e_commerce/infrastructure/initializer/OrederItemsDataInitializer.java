package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.repository.OrderItemsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class OrederItemsDataInitializer {

    private final OrderItemsRepository orderItemsRepository;

    public OrederItemsDataInitializer(OrderItemsRepository orderItemsRepository) {
        this.orderItemsRepository = orderItemsRepository;
    }

    @PostConstruct
    public void init(){
        log.info("주문 상세 정보 초기 셋팅하기");
        orderItemsRepository.save(new OrderItems(1L,1L,1L,"화양연화",25_000,"PAID",1));
        orderItemsRepository.save(new OrderItems(2L,1L,2L,"아미밤",55_000,"PAID",1));
        orderItemsRepository.save(new OrderItems(3L,2L,3L,"Butter",15_000,"PAID",1));
        orderItemsRepository.save(new OrderItems(4L,2L,5L,"Indigo",45_000,"PAID",1));
        orderItemsRepository.save(new OrderItems(5L,2L,6L,"뱃지",25_000,"PAID",1));
    }



}
