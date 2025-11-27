package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.dto.OrderCreateResponse;
import com.commerce.hhplus_e_commerce.facade.CouponFacade;
import com.commerce.hhplus_e_commerce.facade.ProductFacade;
import com.commerce.hhplus_e_commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 주문 생성 UseCase
 * Facade를 통해 분산 락이 적용된 메서드 호출
 */
@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final ProductFacade productFacade;
    private final CouponFacade couponFacade;
    private final OrderService orderService;

    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        // 1. 재고 확인 및 상품 상태 확인 (락 없음 - 조회)
        List<Product> products = productFacade.validateProducts(req.items());

        // 2. 전체 금액 산출 (락 없음 - 계산)
        int totalPrice = productFacade.calculateTotalPrice(products, req.items());

        // 3. 쿠폰 검증 및 할인 금액 계산 (락 없음 - 조회)
        int discountPrice = 0;
        if (req.couponId() != null) {
            couponFacade.validateCoupon(req.userId(), req.couponId());
            discountPrice = couponFacade.getDiscountAmount(req.couponId(), totalPrice);
        }

        // 4. 재고 차감 (분산 락 적용 - ProductFacade 내부)
        productFacade.minusStock(req.items());

        // 5. 주문 생성
        Order order = orderService.createOrder(
                req.userId(),
                totalPrice,
                discountPrice,
                req.couponId()
        );

        return OrderCreateResponse.from(order, products, req.items());
    }
}
