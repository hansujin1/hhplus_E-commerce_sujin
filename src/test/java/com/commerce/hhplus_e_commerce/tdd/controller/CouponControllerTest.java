package com.commerce.hhplus_e_commerce.tdd.controller;

import com.commerce.hhplus_e_commerce.controller.CouponController;
import com.commerce.hhplus_e_commerce.dto.CouponIssueRequest;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.useCase.CouponIssueUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(CouponController.class)
@DisplayName("쿠폰 발급 Controller 테스트")
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CouponIssueUseCase couponIssueUseCase;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_Success() throws Exception {

        Long couponId = 202510L;
        Long userId = 99L;

        when(couponIssueUseCase.issue(eq(couponId), any(CouponIssueRequest.class)))
                .thenReturn(new CouponIssueResponse(20251030L, couponId));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/coupon/{couponId}/issue", couponId) // ← PATCH + 복수형 /coupons
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CouponIssueRequest(userId))) // ← 요청 DTO!
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCouponId").value(20251030))
                .andExpect(jsonPath("$.coupon_id").value(202510));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 존재하지 않는 쿠폰")
    void issueCoupon_NotFound() throws Exception {

        Long couponId = 999L;
        CouponIssueRequest resp = new CouponIssueRequest(100L);

        when(couponIssueUseCase.issue(eq(couponId), any(CouponIssueRequest.class)))
                .thenThrow(new RuntimeException("쿠폰을 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/coupon/{couponId}/issue", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resp)))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급받은 쿠폰")
    void issueCoupon_AlreadyIssued() throws Exception {

        Long couponId = 1L;
        CouponIssueRequest resp = new CouponIssueRequest(100L);

        when(couponIssueUseCase.issue(eq(couponId), any(CouponIssueRequest.class)))
                .thenThrow(new RuntimeException("이미 발급받은 쿠폰입니다."));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/coupon/{couponId}/issue", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resp)))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

}
