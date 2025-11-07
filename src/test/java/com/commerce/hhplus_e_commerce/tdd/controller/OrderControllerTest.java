package com.commerce.hhplus_e_commerce.tdd.controller;

import com.commerce.hhplus_e_commerce.controller.OrderController;
import com.commerce.hhplus_e_commerce.domain.enums.OrderItemStatus;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.dto.OrderCreateResponse;
import com.commerce.hhplus_e_commerce.dto.PaymentRequest;
import com.commerce.hhplus_e_commerce.dto.PaymentResponse;
import com.commerce.hhplus_e_commerce.useCase.CreateOrderUseCase;
import com.commerce.hhplus_e_commerce.useCase.PaymentUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("상품 주문 결제용 controller test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CreateOrderUseCase createOrderUseCase;
    @MockBean
    PaymentUseCase paymentUseCase;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() throws Exception {
        OrderCreateRequest req = new OrderCreateRequest(
                1L,
                List.of(
                        new OrderCreateRequest.Item(1L, 2),
                        new OrderCreateRequest.Item(2L, 1)
                ),
                10L
        );

        var items = List.of(
                new OrderCreateResponse.LineItem(1L,"화양연화", 2, 100_000, 200_000),
                new OrderCreateResponse.LineItem(2L,"아미밤", 1, 50_000, 50_000)
        );
        OrderCreateResponse resp = new OrderCreateResponse(
                100L,
                items,
                250_000L,  // subtotal
                25_000L,   // discountPreview
                225_000L,  // total
                OrderItemStatus.PENDING
        );

        when(createOrderUseCase.createOrder(any())).thenReturn(resp);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.subtotalAmount").value(250000))
                .andExpect(jsonPath("$.discountPreview").value(25000))
                .andExpect(jsonPath("$.totalAmount").value(225000))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].unit_price").value(100_000))
                .andExpect(jsonPath("$.items[0].subtotal").value(200_000));
    }

    @Test
    @DisplayName("결제 성공 - 쿠폰 없이 포인트 결제")
    void pay_success_withoutCoupon() throws Exception {
        Long orderId = 100L;
        PaymentRequest req = new PaymentRequest(1L,null);

        PaymentResponse resp = new PaymentResponse(
                100L,
                150_000,  // originalAmount
                OrderItemStatus.PAID,
                    "QUEUED"
        );

        when(paymentUseCase.payOrder(eq(orderId), any(PaymentRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/orders/{orderId}/payment", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.status").value(OrderItemStatus.PAID))
                .andExpect(jsonPath("$.finalAmount").value(150000))
                .andExpect(jsonPath("$.remainingPoint").value("QUEUED"));
    }

    @Test
    @DisplayName("결제 성공 - 쿠폰 사용 + 포인트 결제")
    void pay_success_withCoupon() throws Exception {
        Long orderId = 100L;
        PaymentRequest req = new PaymentRequest(1L, 1L);

        PaymentResponse resp = new PaymentResponse(
                100L,
                135_000,
                OrderItemStatus.PAID,
                "QUEUED"
        );

        when(paymentUseCase.payOrder(eq(orderId), any(PaymentRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/orders/{orderId}/payment", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.finalAmount").value(135000))
                .andExpect(jsonPath("$.status").value(OrderItemStatus.PAID))
                .andExpect(jsonPath("$.remainingPoint").value("QUEUED"));
    }

    @Test
    @DisplayName("결제 실패 - 포인트 부족 → 5xx")
    void pay_fail_insufficientPoint() throws Exception {
        Long orderId = 100L;
        PaymentRequest req = new PaymentRequest(1L, null);

        when(paymentUseCase.payOrder(eq(orderId), any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("포인트가 부족합니다."));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/orders/{orderId}/payment", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().is5xxServerError());
    }

}

