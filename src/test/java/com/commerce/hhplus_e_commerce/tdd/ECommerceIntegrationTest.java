package com.commerce.hhplus_e_commerce.tdd;

import com.commerce.hhplus_e_commerce.domain.*;
import com.commerce.hhplus_e_commerce.domain.enums.*;
import com.commerce.hhplus_e_commerce.dto.*;
import com.commerce.hhplus_e_commerce.repository.*;
import com.commerce.hhplus_e_commerce.useCase.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ECommerceIntegrationTest {

    @Autowired
    private CouponIssueUseCase couponIssueUseCase;

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Product product1;
    private Product product2;
    private Coupon coupon;

    LocalDate startDate1 = LocalDate.of(2025, Month.JANUARY, 1);
    LocalDate endDate1 = LocalDate.of(2025, Month.DECEMBER, 31);

    @BeforeEach
    void setUp() {
        user = new User("김남준", 100_000);
        userRepository.save(user);

        product1 = new Product("화양연화", 100, 25_000, ProductStatus.SALE, 150);
        productRepository.save(product1);

        product2 = new Product("뱃지", 0, 25_000, ProductStatus.SOLD_OUT, 950);
        productRepository.save(product2);

        coupon = new Coupon("첫구매 15% 할인 쿠폰", 0.15, DiscountType.RATE,
                1000, 100, startDate1, endDate1, 30, CouponStatus.ISSUING);
        couponRepository.save(coupon);
    }

    @Test
    @DisplayName("통합 테스트: 쿠폰 발급 -> 주문 생성 -> 결제 완료")
    void fullECommerceFlow() {
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(user.getUserId());
        CouponIssueResponse couponIssueResponse = couponIssueUseCase.issue(coupon.getCouponId(), couponIssueRequest);

        assertThat(couponIssueResponse.userCouponId()).isNotNull();
        assertThat(couponIssueResponse.couponId()).isEqualTo(coupon.getCouponId());

        List<OrderCreateRequest.Item> items = List.of(
                new OrderCreateRequest.Item(product1.getProductId(), 2) // 화양연화 2개
        );

        OrderCreateRequest orderRequest = new OrderCreateRequest(
                user.getUserId(),
                items,
                couponIssueResponse.couponId()
        );

        OrderCreateResponse orderResponse = createOrderUseCase.createOrder(orderRequest);

        assertThat(orderResponse.orderId()).isNotNull();
        assertThat(orderResponse.subtotalAmount()).isEqualTo(50_000);
        assertThat(orderResponse.totalAmount()).isEqualTo(42_500);
        assertThat(orderResponse.status()).isEqualTo(OrderItemStatus.PENDING);
        assertThat(orderResponse.items()).hasSize(1);

        Product updatedProduct1 = productRepository.selectByProductId(product1.getProductId());
        assertThat(updatedProduct1.getStock()).isEqualTo(98);

        PaymentRequest paymentRequest = new PaymentRequest(user.getUserId(),couponIssueResponse.userCouponId());
        PaymentResponse paymentResponse = paymentUseCase.payOrder(orderResponse.orderId(), paymentRequest);

        assertThat(paymentResponse.orderId()).isEqualTo(orderResponse.orderId());
        assertThat(paymentResponse.paidAmount()).isEqualTo(42_500);

        User updatedUser = userRepository.findByUserId(user.getUserId()).orElseThrow();
        assertThat(updatedUser.getPoint()).isEqualTo(57_500);

        Order order = orderRepository.findByOrderId(orderResponse.orderId()).orElseThrow();

        UserCoupon userCoupon = userCouponRepository.findUserCoupon(couponIssueResponse.userCouponId(),user.getUserId()).orElseThrow();
        assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
    }


    @Test
    @DisplayName("통합 테스트: 품절 상품 주문 실패")
    void orderFailDueToSoldOut() {
        List<OrderCreateRequest.Item> items = List.of(
                new OrderCreateRequest.Item(product2.getProductId(), 1) // 뱃지 (품절)
        );

        OrderCreateRequest orderRequest = new OrderCreateRequest(
                user.getUserId(),
                items,
                null
        );

        // When & Then
        assertThatThrownBy(() -> createOrderUseCase.createOrder(orderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("품절");
    }

    @Test
    @DisplayName("통합 테스트: 재고 부족으로 주문 실패")
    void orderFailDueToInsufficientStock() {
        List<OrderCreateRequest.Item> items = List.of(
                new OrderCreateRequest.Item(product1.getProductId(), 200)
        );

        OrderCreateRequest orderRequest = new OrderCreateRequest(
                user.getUserId(),
                items,
                null
        );

        // When & Then
        assertThatThrownBy(() -> createOrderUseCase.createOrder(orderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고");
    }


    @Test
    @DisplayName("통합 테스트: 만료된 쿠폰 사용 시도")
    void expiredCouponUsage() {
        LocalDate expiredStart = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate expiredEnd = LocalDate.of(2024, Month.DECEMBER, 31);

        Coupon expiredCoupon = new Coupon("만료된 쿠폰", 0.20, DiscountType.FIXED,
                100, 10, expiredStart, expiredEnd, 30, CouponStatus.EXPIRED);
        couponRepository.save(expiredCoupon);

        UserCoupon userCoupon = new UserCoupon(user.getUserId(), coupon.getCouponId(), UserCouponStatus.EXPIRED, LocalDate.now().plusDays(30));
        userCouponRepository.save(userCoupon);

        List<OrderCreateRequest.Item> items = List.of(
                new OrderCreateRequest.Item(product1.getProductId(), 1)
        );

        OrderCreateRequest orderRequest = new OrderCreateRequest(
                user.getUserId(),
                items,
                expiredCoupon.getCouponId()
        );

        assertThatThrownBy(() -> createOrderUseCase.createOrder(orderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("만료");
    }

    @Test
    @DisplayName("통합 테스트: 쿠폰 중복 발급 방지")
    void preventDuplicateCouponIssue() {
        CouponIssueRequest couponRequest = new CouponIssueRequest(user.getUserId());
        CouponIssueResponse firstResponse = couponIssueUseCase.issue(coupon.getCouponId(), couponRequest);

        assertThat(firstResponse.userCouponId()).isNotNull();

        assertThatThrownBy(() -> couponIssueUseCase.issue(coupon.getCouponId(), couponRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 발급");
    }

    @Test
    @DisplayName("통합 테스트: 사용한 쿠폰 재사용 방지")
    void preventUsedCouponReuse() {
        CouponIssueRequest couponRequest = new CouponIssueRequest(user.getUserId());
        CouponIssueResponse couponResponse = couponIssueUseCase.issue(coupon.getCouponId(), couponRequest);

        List<OrderCreateRequest.Item> items1 = List.of(
                new OrderCreateRequest.Item(product1.getProductId(), 1)
        );
        OrderCreateRequest orderRequest1 = new OrderCreateRequest(
                user.getUserId(),
                items1,
                couponResponse.couponId()
        );
        OrderCreateResponse orderResponse1 = createOrderUseCase.createOrder(orderRequest1);

        PaymentRequest paymentRequest1 = new PaymentRequest(user.getUserId(),couponResponse.couponId());
        paymentUseCase.payOrder(orderResponse1.orderId(), paymentRequest1);

        List<OrderCreateRequest.Item> items2 = List.of(
                new OrderCreateRequest.Item(product1.getProductId(), 1)
        );
        OrderCreateRequest orderRequest2 = new OrderCreateRequest(
                user.getUserId(),
                items2,
                couponResponse.couponId()
        );

        assertThatThrownBy(() -> createOrderUseCase.createOrder(orderRequest2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용");
    }
}