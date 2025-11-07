package com.commerce.hhplus_e_commerce.tdd.controller;

import com.commerce.hhplus_e_commerce.controller.OrderController;
import com.commerce.hhplus_e_commerce.dto.OrderCreateResponse;
import com.commerce.hhplus_e_commerce.dto.PaymentResponse;
import com.commerce.hhplus_e_commerce.useCase.CreateOrderUseCase;
import com.commerce.hhplus_e_commerce.useCase.PaymentUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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


    @Test
    @DisplayName("주문 생성 API가 정상 동작한다")
    void createOrder_SmokeTest() throws Exception {
        when(createOrderUseCase.createOrder(any()))
                .thenReturn(new OrderCreateResponse(1L, List.of(), 0L, 0L, 0L, null));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"items\":[],\"couponId\":null}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("결제 API가 정상 동작한다")
    void payment_SmokeTest() throws Exception {
        when(paymentUseCase.payOrder(any(), any()))
                .thenReturn(new PaymentResponse(1L, 100, null, "OK"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/{orderId}/payment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1}"))
                .andExpect(status().isOk());
    }

}

