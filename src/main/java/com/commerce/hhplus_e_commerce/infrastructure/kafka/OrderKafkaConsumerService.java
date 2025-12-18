package com.commerce.hhplus_e_commerce.infrastructure.kafka;

import com.commerce.hhplus_e_commerce.config.KafkaTopicConfig;
import com.commerce.hhplus_e_commerce.event.OrderCompletedEvent;
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
public class OrderKafkaConsumerService {

    @KafkaListener(
        topics = KafkaTopicConfig.ORDER_COMPLETED_TOPIC,
        groupId = "order-data-platform-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCompletedForDataPlatform(
            @Payload OrderCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Data Platform: Order completed event received");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Order ID: {}, User ID: {}, Amount: {}, Status: {}", 
            event.getOrderId(), 
            event.getUserId(), 
            event.getTotalAmount(),
            event.getOrderStatus());
        
        sendToDataPlatform(event);
        log.info("Order info sent to data platform");
    }
    @KafkaListener(
        topics = KafkaTopicConfig.ORDER_COMPLETED_TOPIC,
        groupId = "order-notification-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCompletedForNotification(
            @Payload OrderCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Notification Service: Order completed event received");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Order ID: {}, User ID: {}", event.getOrderId(), event.getUserId());
        
        sendNotification(event);
        log.info("Order notification sent");
    }

    private void sendToDataPlatform(OrderCompletedEvent event) {
        try {
            Thread.sleep(100);
            log.info("Mock API: Data platform request successful");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error sending to data platform", e);
        }
    }

    private void sendNotification(OrderCompletedEvent event) {
        try {
            Thread.sleep(50);
            log.info("Mock API: Notification sent successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error sending notification", e);
        }
    }
}
