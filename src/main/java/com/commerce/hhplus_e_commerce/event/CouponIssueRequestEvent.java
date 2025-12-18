package com.commerce.hhplus_e_commerce.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequestEvent {
    private Long couponId;
    private Long userId;
    private String requestId;
}