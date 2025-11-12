package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.ProductStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Product {
    private Long productId;
    private final String productName;
    private int stock;
    private final int price;
    private ProductStatus status;
    private final int popularityScore;
    private final LocalDate createdDt;

    public void productId(Long productId) {
        this.productId = productId;
    }

    public  Product(Long productId, String productName, int stock, int price, ProductStatus status,
                    int popularityScore, LocalDate createdDt) {
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.popularityScore = popularityScore;
        this.createdDt = createdDt;
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
