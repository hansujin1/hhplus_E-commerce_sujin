package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
public class ProductDataInitializer {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init(){
        log.info("ProductDataInitializer.init()");
        productRepository.save(new Product(1L,"화양연화",100,25_000,"sale",150,new Date()));
        productRepository.save(new Product(2L,"아미밤"  ,200,55_000,"sale",550,new Date()));
        productRepository.save(new Product(3L,"Butter" ,60,15_000,"sale",600,new Date()));
        productRepository.save(new Product(4L,"포토북"  ,0,85_000,"soldOut",900,new Date()));
        productRepository.save(new Product(5L,"Indigo",350,45_000,"sale",1_550,new Date()));
        productRepository.save(new Product(6L,"뱃지",0,25_000,"soldOut",950,new Date()));
    }




}
