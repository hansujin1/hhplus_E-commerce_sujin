package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {
    ISSUING("발급중"),
    SOLD_OUT("품절"),
    EXPIRED("만료");

    private final String description;
}
