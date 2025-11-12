package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {
    RATE("비율 할인"),
    FIXED("금액 할인");

    private final String description;
}
