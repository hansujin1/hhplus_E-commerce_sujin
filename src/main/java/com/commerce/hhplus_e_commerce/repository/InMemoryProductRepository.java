package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository implements ProductRepository{

    private final Map<Long, Product> productMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        if(product.getProductId()==null){
            product.productId(idGenerator.getAndIncrement());
        }
        productMap.put(product.getProductId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Product selectByProductId(Long productId) {
        if (productMap.get(productId) == null) {
            throw new IllegalArgumentException("ProductId is null");
        }
        return productMap.get(productId);
    }

    @Override
    public List<Product> findTopProductsByPopularity() {
        return productMap.values().stream()
                         .sorted(Comparator.comparingInt(Product::getPopularityScore).reversed())
                         .limit(10)
                         .collect(Collectors.toList());
    }


}
