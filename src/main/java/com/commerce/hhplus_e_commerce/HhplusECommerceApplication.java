package com.commerce.hhplus_e_commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class HhplusECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhplusECommerceApplication.class, args);
    }

}
