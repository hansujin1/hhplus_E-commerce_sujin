package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.ProductStatus;
import lombok.Getter;

import java.util.Date;

@Getter
public class Product {
    private Long product_id;
    private final String product_name;
    private int stock;
    private final int price;
    private ProductStatus status;
    private final int popularity_score;
    private final Date created_at;

    public void productId(Long product_id) {
        this.product_id = product_id;
    }

    public  Product(Long product_id, String product_name, int stock, int price, ProductStatus status,
                    int popularity_score, Date created_at) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.popularity_score = popularity_score;
        this.created_at = created_at;
    }


    /** 재고 감소*/
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    /** 판매 가능 여부 확인 */
    public boolean isAvailable() {
        return this.status == ProductStatus.SALE && this.stock > 0;
    }

    /**상품 가격 계산 */
    public int calculatePrice(int quantity) {
        return this.price * quantity;
    }

    /** 재고 복원 */
    public void restoreStock(int quantity) {
        stock += quantity;
    }

}
