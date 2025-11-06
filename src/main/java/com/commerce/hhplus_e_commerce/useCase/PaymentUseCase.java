package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.dto.PaymentRequest;
import com.commerce.hhplus_e_commerce.dto.PaymentResponse;

public class PaymentUseCase {

    /*
    1. 주문 롹인하기
    2. 결제 처리
    3. 결제 성공 시, 주문상태변경하기
    ---
    결제 실패 시, 주문 실패 및 재고 원복시키기
     */

    public PaymentResponse payOrder(String orderId, PaymentRequest req) {

    }
}
