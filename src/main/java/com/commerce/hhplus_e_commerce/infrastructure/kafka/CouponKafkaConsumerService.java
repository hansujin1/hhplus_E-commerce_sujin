package com.commerce.hhplus_e_commerce.infrastructure.kafka;

import com.commerce.hhplus_e_commerce.config.KafkaTopicConfig;
import com.commerce.hhplus_e_commerce.event.CouponIssueRequestEvent;
import com.commerce.hhplus_e_commerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponKafkaConsumerService {

    private final CouponService couponService;

    @KafkaListener(
        topics = KafkaTopicConfig.COUPON_ISSUE_REQUEST_TOPIC,
        groupId = "coupon-issue-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCouponIssueRequest(
            @Payload CouponIssueRequestEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Coupon issue request event received");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Coupon ID: {}, User ID: {}, Request ID: {}", 
            event.getCouponId(), 
            event.getUserId(), 
            event.getRequestId());
        
        try {
            couponService.issueCouponByKafka(event.getCouponId(), event.getUserId());
            
            log.info("Coupon issued successfully: couponId={}, userId={}", 
                event.getCouponId(), event.getUserId());
                
        } catch (Exception e) {
            log.error("Failed to issue coupon: couponId={}, userId={}, error={}", 
                event.getCouponId(), event.getUserId(), e.getMessage(), e);
        }
    }
}