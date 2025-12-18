package com.commerce.hhplus_e_commerce.controller;

import com.commerce.hhplus_e_commerce.dto.CouponIssueRequest;
import com.commerce.hhplus_e_commerce.dto.CouponIssueResponse;
import com.commerce.hhplus_e_commerce.service.CouponService;
import com.commerce.hhplus_e_commerce.useCase.CouponIssueUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Coupon", description = "선착순 쿠폰 발급 API")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponIssueUseCase couponIssueUseCase;
    private final CouponService couponService;

    //쿠폰 생성 요청
    @Operation(summary = "선착순 쿠폰 발급", description = "잔여 수량 내에서만 발급.")
    @PatchMapping("/{couponId}/issue")
    public CouponIssueResponse issueCoupon(@PathVariable("couponId") Long couponId
                                                          , @RequestBody CouponIssueRequest req){

        return couponIssueUseCase.issue(couponId,req);
    }

    @Operation(summary = "선착순 쿠폰 발급 (비동기 - Kafka)", 
               description = "쿠폰 발급 요청을 Kafka로 전송하고 즉시 응답")
    @PostMapping("/{couponId}/issue-async")
    public ResponseEntity<Map<String, String>> issueCouponAsync(
            @PathVariable("couponId") Long couponId,
            @RequestBody CouponIssueRequest req) {
        
        String requestId = couponService.issueCouponAsync(req.userId(), couponId);
        
        return ResponseEntity.accepted()
                .body(Map.of(
                    "message", "쿠폰 발급 요청이 접수되었습니다.",
                    "requestId", requestId,
                    "status", "PENDING"
                ));
    }


}
