package com.commerce.hhplus_e_commerce.controller;

import com.commerce.hhplus_e_commerce.dto.*;
import com.commerce.hhplus_e_commerce.useCase.CreateOrderUseCase;
import com.commerce.hhplus_e_commerce.useCase.PaymentUseCase;
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

    private final CreateOrderUseCase createOrderUseCase;
    private final PaymentUseCase paymentUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, PaymentUseCase paymentUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.paymentUseCase = paymentUseCase;
    }


    @Operation(summary = "주문 생성(PENDING)", description = "결제전 주문생성")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderCreateResponse> create(@Valid @RequestBody OrderCreateRequest req) {

        return ResponseEntity.ok(createOrderUseCase.createOrder(req));
    }

    @Operation(summary = "결제 처리(포인트 + 쿠폰)", description = "쿠폰 유효성 확인 → 할인 → 포인트 차감 → 주문 PAID")
    @PostMapping(value = "/{orderId}/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest req) {

        return ResponseEntity.ok(paymentUseCase.payOrder(orderId, req));

    }
}
