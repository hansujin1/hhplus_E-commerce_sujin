package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.PointHis;
import com.commerce.hhplus_e_commerce.repository.PointHisRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.PointHisJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHisRepositoryImpl implements PointHisRepository {
    private final PointHisJpaRepository pointHisJpaRepository;

    @Override
    public PointHis save(PointHis pointHis) {
        return pointHisJpaRepository.save(pointHis);
    }

    @Override
    public List<PointHis> findPointHisByUserId(Long userId) {
        return pointHisJpaRepository.findByUserIdOrderByCreatedDtDesc(userId);
    }

    @Override
    public long count() {
        return pointHisJpaRepository.count();
    }

}
