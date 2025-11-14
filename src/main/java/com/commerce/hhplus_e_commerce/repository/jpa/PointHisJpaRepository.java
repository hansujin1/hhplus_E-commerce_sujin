package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.PointHis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PointHisJpaRepository extends JpaRepository<PointHis,Long> {

    List<PointHis> findByUserIdOrderByCreatedDtDesc(Long userId);

}
