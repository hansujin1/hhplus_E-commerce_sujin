package com.commerce.hhplus_e_commerce.facade;

import com.commerce.hhplus_e_commerce.config.DistributedLock;
import com.commerce.hhplus_e_commerce.domain.OrderItems;
import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import com.commerce.hhplus_e_commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public List<Product> validateProducts(List<OrderCreateRequest.Item> items) {
        return productService.validateProducts(items);
    }

    public int calculateTotalPrice(List<Product> products, List<OrderCreateRequest.Item> items) {
        return productService.calculateTotalPrice(products, items);
    }

    public void minusStock(List<OrderCreateRequest.Item> items) {
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

        for (Map.Entry<Long, Integer> entry : qtyByProduct.entrySet()) {
            decreaseStockWithLock(entry.getKey(), entry.getValue());
        }
    }

    @DistributedLock(
        key = "'product:stock:' + #productId",
        waitTime = 5L,
        leaseTime = 3L
    )
    public void decreaseStockWithLock(Long productId, int quantity) {
        Product product = productRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new IllegalStateException("상품을 찾을 수 없습니다: " + productId));

        if (product.getStock() < quantity) {
            throw new IllegalStateException(
                    "재고 부족: " + product.getProductName() +
                    " (요청: " + quantity + ", 보유: " + product.getStock() + ")"
            );
        }

        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    public void restoreStockByOrderItems(List<OrderItems> orderItems) {
        productService.restoreStockByOrderItems(orderItems);
    }
}