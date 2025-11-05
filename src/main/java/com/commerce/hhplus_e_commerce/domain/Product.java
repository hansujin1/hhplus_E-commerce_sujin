package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Product {
    private Long product_id;
    private String product_name;
    private int stock;
    private int price;
    private String status;
    private int popularity_score;
    private Date created_at;

    public Product() {}

    public  Product(Long product_id, String product_name, int stock, int price, String status, int popularity_score, Date created_at) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.popularity_score = popularity_score;
        this.created_at = created_at;
    }

}
