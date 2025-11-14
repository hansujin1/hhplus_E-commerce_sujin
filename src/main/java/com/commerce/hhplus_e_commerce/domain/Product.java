package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name", nullable = false)
    private  String productName;
    @Column(name = "stock", nullable = false)
    private int stock;
    @Column(name = "price", nullable = false)
    private  int price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;
    @Column(name = "popularity_score")
    private  int popularityScore;
    @Column(name = "created_dt", nullable = false)
    private  LocalDate createdDt;

    public void productId(Long productId) {
        this.productId = productId;
    }

    public  Product( String productName, int stock, int price, ProductStatus status,
                    int popularityScore) {
        this.productName = productName;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.popularityScore = popularityScore;
        this.createdDt = LocalDate.now();
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
