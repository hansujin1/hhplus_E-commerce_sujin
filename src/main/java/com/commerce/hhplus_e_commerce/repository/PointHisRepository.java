package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.PointHis;

import java.util.List;

public interface PointHisRepository {

    PointHis save(PointHis pointHis);

    List<PointHis> findPointHisByUserId(Long userId);

    long count();

}
