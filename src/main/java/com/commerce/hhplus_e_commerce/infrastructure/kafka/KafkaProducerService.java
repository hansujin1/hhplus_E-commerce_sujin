package com.commerce.hhplus_e_commerce.infrastructure.kafka;

import com.commerce.hhplus_e_commerce.config.KafkaTopicConfig;
import com.commerce.hhplus_e_commerce.event.CouponIssueRequestEvent;
import com.commerce.hhplus_e_commerce.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCompletedEvent(OrderCompletedEvent event) {
        String key = String.valueOf(event.getOrderId());
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(KafkaTopicConfig.ORDER_COMPLETED_TOPIC, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order completed event published: orderId={}, offset={}", 
                    event.getOrderId(), 
                    result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish order completed event: orderId={}", event.getOrderId(), ex);
            }
        });
    }

    public void publishCouponIssueRequestEvent(CouponIssueRequestEvent event) {
        String key = String.valueOf(event.getCouponId());
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(KafkaTopicConfig.COUPON_ISSUE_REQUEST_TOPIC, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Coupon issue request published: couponId={}, userId={}, partition={}, offset={}", 
                    event.getCouponId(),
                    event.getUserId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish coupon issue request: couponId={}, userId={}", 
                    event.getCouponId(), event.getUserId(), ex);
            }
        });
    }
}