package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCouponStatus
{
    ACTIVE("미사용"),   // 사용 가능
    USED("사용"),     // 이미 사용됨
    EXPIRED("만료");  // 만료됨

    private final String description;
}
