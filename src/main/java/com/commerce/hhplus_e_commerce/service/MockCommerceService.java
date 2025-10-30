package com.commerce.hhplus_e_commerce.service;

import com.commerce.hhplus_e_commerce.dto.*;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MockCommerceService {

    private final Map<String, Map<String, Object>> users = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> products = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> coupons = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> userCoupons = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> orders = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> orderItems = new ConcurrentHashMap<>();

    public MockCommerceService() {
        // Seed USERS
        users.put("sujin01", new HashMap<>(Map.of("user_name","sujin","point",100_000L,"created_dt","2025-01-01T00:00:00Z")));

        // Seed PRODUCTS
        products.put("P001", new HashMap<>(Map.of("product_name","노트북","price",80_000L,"stock",10,"state","SALE","created_at","2025-01-02T00:00:00Z")));
        products.put("P002", new HashMap<>(Map.of("product_name","키보드","price",20_000L,"stock",50,"state","SALE","created_at","2025-01-02T00:00:00Z")));

        // Seed COUPONS
        coupons.put("F-2025Sale", new HashMap<>(Map.of(
                "coupon_name","가을할인프로모션",
                "discount_rate",10,
                "type", "PERCENT",
                "total_quantity",100,
                "issued_quantity",0,
                "start_dt","2025-01-01T00:00:00Z",
                "end_dt","2025-12-31T23:59:59Z"
        )));
    }

    //쿠폰 발급
    public CouponIssueResponse issueCoupon(String couponId, CouponIssueRequest couponIssueRequest) {
        Map<String,Object> coupon = coupons.get(couponId);
        if (coupon == null){
            throw new IllegalArgumentException("쿠폰없음");
        }
        
        String now = Instant.now().toString(); //현재시간

        int issued = (int)coupon.get("issued_quantity");
        int total  = (int)coupon.get("total_quantity");
        if (issued >= total) throw new IllegalStateException("선착순 쿠폰 매진");

        // 사용자-쿠폰 중복(1인 1매) 방지: userCoupons에서 userId & couponId 중복 체크
        boolean exists = userCoupons.values().stream()
                .anyMatch(a -> couponIssueRequest.getUserId().equals(a.get("user_id")) && couponId.equals(a.get("coupon_id")));
        if (exists){
            throw new IllegalStateException("이미 발급받은 쿠폰");
        }

        // 발급 처리
        coupon.put("issued_quantity", issued + 1);
        String userCouponId = "UC-" + System.currentTimeMillis();
        String expiresAt = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        userCoupons.put(userCouponId, new HashMap<>(Map.of(
                "user_id", couponIssueRequest.getUserId(),
                "coupon_id", couponId,
                "status", "AVAILABLE",
                "issued_at", now,
                "used", null,
                "expires_at", expiresAt
        )));

        return new CouponIssueResponse(
                userCouponId,
                coupon.get("coupon_name"),
                (int)coupon.get("discount_rate"),
                (String) coupon.get("type"),
                expiresAt,
                total - (issued + 1)
        );
    }


    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        // 금액 계산 + 재고 즉시 차감 (모든 품목 성공해야 커밋)
        long subtotal = 0L;
        List<OrderCreateResponse.LineItem> lines = new ArrayList<>();

        // 1) 상품/수량 유효성 및 현재 가격으로 금액 계산
        for (var it : req.items()) {
            var product = products.get(it.productId());
            if (product == null){
                throw new IllegalArgumentException("상품없음: " + it.productId());
            }
            long price = (long) product.get("price");
            long sub = price * it.quantity();
            subtotal += sub;
            lines.add(new OrderCreateResponse.LineItem(
                    it.productId(), (String) product.get("product_name"), it.quantity(), price, sub));
        }

        // 2) *** 재고 확인 + 즉시 차감***
        synchronized (this) {
            // 2-1) 전 품목 재고 충분한지 먼저 검사
            for (var it : req.items()) {
                var pd = products.get(it.productId());
                int stock = (int) pd.get("stock");
                if (stock < it.quantity()) {
                    throw new IllegalStateException("재고 부족: " + it.productId());
                }
            }
            // 2-2) 전 품목 차감 커밋
            for (var it : req.items()) {
                var pd = products.get(it.productId());
                pd.put("stock", ((int) pd.get("stock")) - it.quantity());
            }
        }

        // 3) 쿠폰 할인
        long discountPreview = 0L;
        if (req.couponId() != null) {
            var coupon = coupons.get(req.couponId());
            if (coupon != null && "PERCENT".equals(coupon.get("type"))) {
                int rate = (int) coupon.get("discount_rate");
                discountPreview = Math.floorDiv(subtotal * rate, 100);
            }
        }
        long total = Math.max(0, subtotal - discountPreview);

        // 4) 주문 저장 (PENDING)
        String orderId = "O-" + System.currentTimeMillis();
        orders.put(orderId, new HashMap<>(Map.of(
                "user_id", req.userId(),
                "status", "PENDING",
                "subtotal", subtotal,
                "coupon_id", req.couponId(),
                "created_at", Instant.now().toString()
        )));
        // 품목 저장
        orderItems.put(orderId, req.items().stream().map(it -> Map.<String, Object>of(
                "product_id", it.productId(),
                "quantity", it.quantity()
        )).collect(Collectors.toList()));

        return new OrderCreateResponse(orderId, lines, subtotal, discountPreview, total, "PENDING");
    }

    @Synchronized
    public PaymentResponse payOrder(String orderId, String userId) {
        var order = orders.get(orderId);
        if (order == null){
            throw new IllegalArgumentException("주문없음");
        }
        if (!Objects.equals(order.get("user_id"), userId)){
            throw new IllegalStateException("주문 사용자 불일치");
        }
        if (!"PENDING".equals((String) order.get("status"))){
            throw new IllegalStateException("이미 결제 처리됨");
        }

        long subtotal = (long) order.get("subtotal");
        String couponId = (String) order.get("coupon_id");

        // (1) 쿠폰 검증 & 할인 계산
        long discount = 0L;
        if (couponId != null) {
            var coupon = coupons.get(couponId);
            if (coupon == null){
                throw new IllegalStateException("쿠폰없음");
            }

            // 유저가 보유한 'AVAILABLE' 쿠폰인지 체크
            String userCouponKey = findAvailableUserCoupon(userId, couponId);
            if (userCouponKey == null){
                throw new IllegalStateException("보유/사용가능 쿠폰 아님");
            }

            if ("PERCENT".equals(coupon.get("type"))) {
                int rate = (int) coupon.get("discount_rate");
                discount = Math.floorDiv(subtotal * rate, 100);
            }
            // 결제 성공 시점에 사용할 키 임시 저장
            order.put("user_coupon_key", userCouponKey);
        }

        long payAmount = Math.max(0, subtotal - discount);

        // (2) 포인트 확인 & 차감
        var u = users.get(userId);
        long point = (long) u.get("point");
        if (point < payAmount){
            throw new IllegalStateException("포인트 부족");
        }
        u.put("point", point - payAmount);

        // (3) 쿠폰 사용 처리
        if (couponId != null) {
            String key = (String) order.get("user_coupon_key");
            if (key != null) {
                var uc = userCoupons.get(key);
                if (uc != null) {
                    uc.put("status", "USED");
                    uc.put("used_at", Instant.now().toString());
                }
            }
        }

        // (4) 주문 상태 갱신
        order.put("status", "PAID");
        order.put("paid_at", Instant.now().toString());
        order.put("discount", discount);
        order.put("final_amount", payAmount);

        long remaining = (long) users.get(userId).get("point");
        return new PaymentResponse(orderId, payAmount, remaining, "SUCCESS", "QUEUED");
    }

    private String findAvailableUserCoupon(String userId, String couponId) {
        // 상태 AVAILABLE, 만료 전
        Instant now = Instant.now();
        for (var e : userCoupons.entrySet()) {
            var v = e.getValue();
            if (Objects.equals(v.get("user_id"), userId)
                    && Objects.equals(v.get("coupon_id"), couponId)
                    && Objects.equals(v.get("status"), "AVAILABLE")) {
                String exp = (String)v.get("expires_at");
                if (exp == null || Instant.parse(exp).isAfter(now)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }
}
