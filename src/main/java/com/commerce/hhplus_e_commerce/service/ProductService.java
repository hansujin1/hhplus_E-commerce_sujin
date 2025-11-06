package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> validateProducts(List<OrderCreateRequest.Item> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 비어 있습니다.");
        }

        List<Product> result = new ArrayList<>();

        for (OrderCreateRequest.Item item : items) {

            // 1) 상품 조회
            Product product = productRepository.selectByProductId(item.productId());
            if (product == null) {
                throw new IllegalStateException("상품을 찾을 수 없습니다: " + item.productId());
            }

            // 2) 판매 가능 상태 여부 확인
            if (!product.isAvailable()) {
                throw new IllegalStateException("현재 판매할 수 없는 상품입니다: " + product.getProduct_name());
            }

            // 3) 재고 확인
            if (product.getStock() < item.quantity()) {
                throw new IllegalStateException(
                        "재고 부족: " + product.getProduct_name() +
                                " (요청: " + item.quantity() + ", 보유: " + product.getStock() + ")"
                );
            }

            result.add(product);
        }

        return result;
    }

    public int calculateTotalPrice(List<Product> products, List<OrderCreateRequest.Item> items) {

        int total = 0;

        for (OrderCreateRequest.Item item : items) {
            Product product = products.stream()
                    .filter(p -> p.getProduct_id().equals(item.productId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("상품 데이터를 찾을 수 없습니다."));

            total += product.calculatePrice(item.quantity());
        }

        return total;
    }


}
