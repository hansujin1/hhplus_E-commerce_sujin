package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
                throw new IllegalStateException("현재 판매할 수 없는 상품입니다: " + product.getProductName());
            }

            if (product.getStock() < item.quantity()) {
                throw new IllegalStateException(
                        "재고 부족: " + product.getProductName() +
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
                    .filter(p -> Objects.equals(p.getProductId(), item.productId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("상품 데이터를 찾을 수 없습니다."));

            total += product.calculatePrice(item.quantity());
        }

        return total;
    }


    @Transactional
    public void minusStock( @NotEmpty List<OrderCreateRequest.Item> items) {
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

        for(Map.Entry<Long, Integer> entry : qtyByProduct.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findByProductIdWithLock(productId)
                    .orElseThrow(() -> new IllegalStateException("상품을 찾을 수 없습니다: " + productId));

            if (product.getStock() < quantity) {
                throw new IllegalStateException(
                        "재고 부족: " + product.getProductName() + " (요청: " + quantity + ", 보유: " + product.getStock() + ")"
                );
            }

            product.decreaseStock(quantity);

            productRepository.save(product);
        }
    }

    public void restoreStockByOrderItems(List<OrderItems> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) return;

        // 동일 상품 여러 개 합쳐서 복원 (안하면 중복 복원됨)
        Map<Long, Integer> qtyByProduct = new HashMap<>();
        for (OrderItems item : orderItems) {
            qtyByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : qtyByProduct.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();

            Product product = productRepository.selectByProductId(productId);
            if (product == null) continue;

            product.restoreStock(qty);
            productRepository.save(product);
        }
    }
}
