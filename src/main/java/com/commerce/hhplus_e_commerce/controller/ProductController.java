package com.commerce.hhplus_e_commerce.controller;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.ProductResponse;
import com.commerce.hhplus_e_commerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "상품", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "인기 상품 조회", description = "인기도 점수 기준 상위 상품을 조회합니다. (캐시 적용)")
    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Product> products = productService.getPopularProducts(limit);

        List<ProductResponse> response = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}