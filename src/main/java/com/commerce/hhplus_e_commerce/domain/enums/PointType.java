package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType
{
    USE("사용"),
    CHARGE("충전");

    private final String description;
}
