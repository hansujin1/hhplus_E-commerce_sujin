package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Product;

import java.util.List;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    Product selectByProductId(Long productId);

    List<Product> findTopProductsByPopularity();

}
