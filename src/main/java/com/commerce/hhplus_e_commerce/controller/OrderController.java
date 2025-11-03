package com.commerce.hhplus_e_commerce.controller;

import com.commerce.hhplus_e_commerce.dto.*;
import com.commerce.hhplus_e_commerce.service.MockCommerceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Orders", description="주문/결제 API (포인트 + 쿠폰)")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final MockCommerceService service;

    public OrderController(MockCommerceService service) {
        this.service = service;
    }

    @Operation(summary = "주문 생성(PENDING)", description = "쿠폰은 정보로만 저장, 실제 차감은 결제 단계에서 적용")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderCreateResponse> create(@Valid @RequestBody OrderCreateRequest req) {
        return ResponseEntity.ok(service.createOrder(req));
    }

    @Operation(summary = "결제 처리(포인트 + 쿠폰)", description = "쿠폰 유효성 확인 → 할인 → 포인트 차감 → 재고 차감 → 주문 PAID")
    @PostMapping(value = "/{orderId}/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponse> pay(@PathVariable String orderId, @Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.ok(service.payOrder(orderId, req.userId()));

    }
}
