package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.PointHis;
import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import com.commerce.hhplus_e_commerce.repository.PointHisRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
public class PointHisDataInitializer {

    private final PointHisRepository pointHisRepository;

    public PointHisDataInitializer(PointHisRepository pointHisRepository) {
        this.pointHisRepository = pointHisRepository;
    }

    @PostConstruct
    public void init(){
        log.info("User의 포인트 이력 정보 초기 셋팅하기 - 김남준");
        pointHisRepository.save(new PointHis(null,20250202L, PointType.CHARGE,150_000,new Date()));
        pointHisRepository.save(new PointHis(null,20250202L,PointType.USE,50_000,new Date()));

        log.info("User의 포인트 이력 정보 초기 셋팅하기 - 정호석");
        pointHisRepository.save(new PointHis(null,20250222L,PointType.CHARGE,150_000,new Date()));
        pointHisRepository.save(new PointHis(null,20250222L,PointType.USE,25_000,new Date()));
        pointHisRepository.save(new PointHis(null,20250222L,PointType.USE,12_000,new Date()));
    }



}
