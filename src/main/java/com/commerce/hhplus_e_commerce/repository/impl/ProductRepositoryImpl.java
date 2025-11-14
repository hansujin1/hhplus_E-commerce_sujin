package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;


    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public Product selectByProductId(Long productId) {
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("상품을 찾을 수 없습니다: " + productId));
    }

    @Override
    public List<Product> findTopProductsByPopularity() {
        return productJpaRepository.findTop10ByOrderByPopularityScoreDesc();
    }
}
