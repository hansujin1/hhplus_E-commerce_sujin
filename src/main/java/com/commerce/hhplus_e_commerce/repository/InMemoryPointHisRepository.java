package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.PointHis;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPointHisRepository implements PointHisRepository{

    private final Map<Long, PointHis> pointHisMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public PointHis save(PointHis pointHis) {
        if(pointHis.getPointHisId()==null){
            pointHis.pointHisId(idGenerator.getAndIncrement());
        }

        pointHisMap.put(pointHis.getPointHisId(),pointHis);

        return pointHis;
    }

    @Override
    public List<PointHis> findPointHisByUserId(Long userId) {
        if(userId==null){
            throw new IllegalArgumentException("findPointHis -> ID가 넘어오지 않음");
        }

        return pointHisMap.values().stream()
                           .filter(p -> userId.equals(p.getUserId()))
                           .collect(Collectors.toList());
    }
}
