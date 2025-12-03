package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.domain.enums.ProductStatus;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class ProductDataInitializer {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init(){
        if (productRepository.findAll().size() > 0) {
            log.info("Product 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("ProductDataInitializer.init()");
        productRepository.save(new Product("화양연화", 100, 25_000, ProductStatus.SALE, 150));
        productRepository.save(new Product("아미밤", 200, 55_000, ProductStatus.SALE, 550));
        productRepository.save(new Product("Butter", 60, 15_000, ProductStatus.SALE, 600));
        productRepository.save(new Product("포토북", 0, 85_000, ProductStatus.SOLD_OUT, 900));
        productRepository.save(new Product("Indigo", 350, 45_000, ProductStatus.SALE, 1_550));
        productRepository.save(new Product("뱃지", 0, 25_000, ProductStatus.SOLD_OUT, 950));
    }




}
