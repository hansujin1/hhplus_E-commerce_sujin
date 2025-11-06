package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            Product product = productRepository.selectByProductId(item.productId());
            if (product == null) {
                throw new IllegalStateException("상품을 찾을 수 없습니다: " + item.productId());
            }

            if (!product.isAvailable()) {
                throw new IllegalStateException("현재 판매할 수 없는 상품입니다: " + product.getProduct_name());
            }

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


    public void minusStock(List<Product> products, @NotEmpty List<OrderCreateRequest.Item> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("차감할 상품이 없습니다.");
        }

        Map<Long, Integer> qtyByProduct = new HashMap<>();
        for (OrderCreateRequest.Item it : items) {
            if (it.productId() == null || it.quantity() <= 0) {
                throw new IllegalArgumentException("상품ID 또는 수량이 올바르지 않습니다.");
            }
            qtyByProduct.merge(it.productId(), it.quantity(), Integer::sum);
        }

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProduct_id, p -> p));

        for (Map.Entry<Long, Integer> e : qtyByProduct.entrySet()) {
            Long productId = e.getKey();
            int need = e.getValue();

            Product p = productMap.get(productId);
            if (p == null) {
                // 방어: 전달된 products에 없으면 저장소에서 조회 (in-memory/DB 모두 대응)
                p = productRepository.selectByProductId(productId);
                if (p == null) {
                    throw new IllegalStateException("상품을 찾을 수 없습니다: " + productId);
                }
                productMap.put(productId, p);
            }

            p.decreaseStock(need);

            // DB 전환 시엔 UPDATE 의
            productRepository.save(p);
        }
    }
}
