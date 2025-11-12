package com.commerce.hhplus_e_commerce.tdd.controller;

import com.commerce.hhplus_e_commerce.controller.CouponController;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.useCase.CouponIssueUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(CouponController.class)
@DisplayName("쿠폰 발급 Controller 테스트")
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CouponIssueUseCase couponIssueUseCase;

    @Test
    @DisplayName("쿠폰 발급 API가 정상 응답을 반환한다")
    void issueCoupon_SmokeTest() throws Exception {
        // given
        when(couponIssueUseCase.issue(any(), any()))
                .thenReturn(new CouponIssueResponse(1L, 100L));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/coupon/{couponId}/issue", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk());
    }

}
