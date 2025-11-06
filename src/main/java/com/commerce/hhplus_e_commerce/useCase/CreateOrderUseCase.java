package com.commerce.hhplus_e_commerce.useCase;

import com.commerce.hhplus_e_commerce.domain.Order;
import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.dto.OrderCreateResponse;
import com.commerce.hhplus_e_commerce.service.CouponService;
import com.commerce.hhplus_e_commerce.service.OrderService;
import com.commerce.hhplus_e_commerce.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateOrderUseCase {

    /*
    1.상품 상태확인 및 재고 확인하기
    2.쿠폰유효성 check하기
    3.할인금액 계산하기
    4. 포인트 사용가는 여부 판단
    5. 주문생성 및 저장
     */

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;

    public CreateOrderUseCase(ProductService productService, CouponService couponService, OrderService orderService) {
        this.productService = productService;
        this.couponService = couponService;
        this.orderService = orderService;
    }

    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        // 재고 확인 및 상품상태 확인
        List<Product> products = productService.validateProducts(req.items());

        //전체 금액 산출하기
        int totalPrice = productService.calculateTotalPrice(products, req.items());

        int discountPrice = 0;
        if (req.couponId() != null) {
            couponService.validateCoupon(req.userId(), req.couponId());
            discountPrice = couponService.getDiscountAmount(req.couponId(), totalPrice);
        }

        Order order = orderService.createOrder(
                req.userId(),
                totalPrice,
                discountPrice,
                req.couponId()
        );


       return OrderCreateResponse.from(order, products, req.items());
    }




}
