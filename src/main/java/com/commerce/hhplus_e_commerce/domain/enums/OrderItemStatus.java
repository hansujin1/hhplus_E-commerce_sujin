package com.commerce.hhplus_e_commerce.domain.enums;

public enum OrderItemStatus {
    PENDING,
    PAID,
    SHIPPED, //배달중
    DELIVERED, //배달완료
    CANCELED,
    RETURNED,
    FAILED;

}
