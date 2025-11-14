package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductJpaRepository extends JpaRepository<Product,Long> {

    List<Product> findTop10ByOrderByPopularityScoreDesc();

}
