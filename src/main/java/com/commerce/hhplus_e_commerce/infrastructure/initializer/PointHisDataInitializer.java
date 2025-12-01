package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.PointHis;
import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import com.commerce.hhplus_e_commerce.repository.PointHisRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@DependsOn({"userDataInitializer"})
public class PointHisDataInitializer {

    private final PointHisRepository pointHisRepository;

    public PointHisDataInitializer(PointHisRepository pointHisRepository) {
        this.pointHisRepository = pointHisRepository;
    }

    @PostConstruct
    public void init(){
        if (pointHisRepository.count() > 0) {
            log.info("PointHis 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("User의 포인트 이력 정보 초기 셋팅하기 - 김남준");
        pointHisRepository.save(new PointHis(1L, PointType.CHARGE, 150_000));
        pointHisRepository.save(new PointHis(1L, PointType.USE, 50_000));

        log.info("User의 포인트 이력 정보 초기 셋팅하기 - 정호석");
        pointHisRepository.save(new PointHis(2L, PointType.CHARGE, 150_000));
        pointHisRepository.save(new PointHis(2L, PointType.USE, 25_000));
        pointHisRepository.save(new PointHis(2L, PointType.USE, 12_000));
    }



}
