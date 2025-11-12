package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus
{
    SALE("판매"),
    SOLD_OUT("품절"),
    STOPPED("판매 중지");

    private final String description;
}
