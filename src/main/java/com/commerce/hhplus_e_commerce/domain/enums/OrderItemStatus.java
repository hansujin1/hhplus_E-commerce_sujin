package com.commerce.hhplus_e_commerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderItemStatus {
    PENDING("결제 중"),
    PAID("결제 완료"),
    SHIPPED("배송중"), //배달중
    DELIVERED("배송완료"), //배달완료
    CANCELED("취소"),
    RETURNED("환불"),
    FAILED("실패");

    private final String description;

}
