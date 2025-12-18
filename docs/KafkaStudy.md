# Apache Kafka 기초 학습

## 1. Kafka란?

Apache Kafka는 **분산 스트리밍 플랫폼**으로, 대용량의 실시간 데이터를 안정적으로 처리하기 위한 메시지 큐 시스템입니다.

### 주요 특징
- **높은 처리량(High Throughput)**: 초당 수백만 건의 메시지 처리 가능
- **확장성(Scalability)**: 브로커 추가로 수평 확장 가능
- **내구성(Durability)**: 디스크에 메시지 저장으로 데이터 손실 방지
- **분산 시스템**: 여러 서버에 데이터 분산 저장 및 복제

---

## 2. Kafka 핵심 개념

### 2.1 Producer (프로듀서)
- 메시지를 생성하고 Kafka Topic에 **발행(Publish)**하는 주체
- 어느 Partition으로 메시지를 보낼지 결정 (Key 기반 또는 Round-Robin)

**우리 프로젝트 예시:**
```java
// 주문 완료 이벤트 발행
kafkaTemplate.send("order-completed", orderId, orderEvent);
```

### 2.2 Consumer (컨슈머)
- Topic에서 메시지를 **소비(Subscribe)**하는 주체
- Consumer Group 단위로 동작하여 병렬 처리 가능

**우리 프로젝트 예시:**
```java
@KafkaListener(topics = "order-completed", groupId = "order-data-platform-group")
public void consume(OrderCompletedEvent event) {
    // 주문 데이터 처리
}
```

### 2.3 Topic (토픽)
- 메시지가 저장되는 **논리적 채널**
- 여러 Partition으로 분할되어 병렬 처리 가능
- 예: `order-completed`, `coupon-issue-request`

### 2.4 Partition (파티션)
- Topic을 물리적으로 나눈 단위
- **병렬 처리**를 위한 핵심 개념
- 각 Partition은 순서가 보장됨 (같은 Partition 내에서만)

**파티션 동작 방식:**
```
Topic: order-completed (3개 파티션)
┌─────────────┐
│ Partition 0 │ → Consumer 1
├─────────────┤
│ Partition 1 │ → Consumer 2
├─────────────┤
│ Partition 2 │ → Consumer 3
└─────────────┘
```

### 2.6 Offset (오프셋)
- Consumer가 읽은 메시지의 **위치(인덱스)**
- Kafka가 자동으로 관리하여 중복/누락 방지
- Consumer 재시작 시 마지막 읽은 위치부터 재개 가능

### 2.7 Broker (브로커)
- Kafka 서버 인스턴스
- 여러 Broker가 클러스터를 구성하여 분산 처리

---

## 3. Kafka vs 기존 메시지 큐 (RabbitMQ, Redis Pub/Sub)

| 특징 | Kafka |  Redis Pub/Sub |
|------|-------|--------------|
| **메시지 저장** | 디스크에 영구 저장 | 메모리만 (저장 X) |
| **처리량** | 초당 수백만 건 | 초당 수십만 건 |
| **재처리** | 가능 (Offset 조정) |  불가능 |
| **메시지 순서** | Partition 내 보장 |  보장 안 됨 |
| **사용 사례** | 대용량 로그, 이벤트 스트리밍 |  실시간 알림, 채팅 |

---

## 4. 우리 프로젝트에서 Kafka 사용 이유

### 4.1 주문 완료 이벤트 발행
**문제점:**
- 주문 완료 시 여러 작업이 필요 (데이터 분석, 알림 발송, 재고 업데이트 등)
- 동기 처리 시 응답 속도 저하 및 장애 전파

**Kafka 해결책:**
```java
// PaymentService - 트랜잭션 커밋 후 이벤트 발행
TransactionSynchronizationManager.registerSynchronization(
    new TransactionSynchronization() {
        @Override
        public void afterCommit() {
            OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .totalAmount(order.getFinalPrice())
                .build();
            
            kafkaProducerService.publishOrderCompletedEvent(event);
        }
    }
);
```

**장점:**
- ✅ 주문 처리와 후속 작업 분리 (빠른 응답)
- ✅ 다양한 시스템이 같은 이벤트를 독립적으로 소비 가능
- ✅ Consumer 장애 시에도 메시지 보존 (재처리 가능)

### 4.2 쿠폰 발급 요청 처리
**문제점:**
- 대량 쿠폰 발급 시 DB 부하 발생
- 동시성 제어 복잡

**Kafka 해결책:**
```java
// CouponKafkaConsumerService - 순차 처리로 동시성 보장
@KafkaListener(topics = "coupon-issue-request", groupId = "coupon-issue-group")
public void consumeCouponIssueRequest(CouponIssueRequestEvent event) {
    couponService.issueCouponByKafka(event.getCouponId(), event.getUserId());
}
```

**장점:**
- ✅ Partition 내에서 순서 보장 (같은 쿠폰 ID는 같은 Partition으로)
- ✅ Consumer 수 조절로 처리량 제어 가능
- ✅ 실패한 요청 재처리 가능

---

## 5. Kafka 설정 상세

### 5.1 Producer 설정
```java
@Configuration
public class KafkaProducerConfig {
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Kafka 서버 주소
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Key, Value Serializer (객체 → 바이트 변환)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // acks=all: 모든 복제본에 저장 확인 후 응답 (안정성 최대)
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // 전송 실패 시 재시도 횟수
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // 멱등성 활성화 (중복 메시지 방지)
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
```

**주요 설정 설명:**
- `acks=all`: 가장 안전한 설정 (성능 약간 저하)
- `acks=1`: 리더 Partition만 저장 확인 (기본값)
- `acks=0`: 확인 없이 전송 (최고 성능, 메시지 손실 가능)

### 5.2 Consumer 설정
```java
@Configuration
public class KafkaConsumerConfig {
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        // Kafka 서버 주소
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Consumer Group ID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // Key, Value Deserializer (바이트 → 객체 변환)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // 처음 읽을 위치 (earliest: 처음부터, latest: 최신부터)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // JSON 역직렬화 시 신뢰할 패키지
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

### 5.3 Topic 설정
```java
@Configuration
public class KafkaTopicConfig {
    
    @Bean
    public NewTopic orderCompletedTopic() {
        return TopicBuilder.name("order-completed")
                .partitions(3)      // 3개 Partition
                .replicas(1)        // 복제본 1개 (단일 브로커)
                .build();
    }
}
```

---

## 6. 실전 활용 패턴

### 6.1 트랜잭션 커밋 후 이벤트 발행
**WHY?**
- DB 트랜잭션이 롤백되면 이벤트도 발행되면 안 됨
- 트랜잭션 커밋 후 이벤트 발행으로 데이터 일관성 보장

**HOW?**
```java
@Transactional
public Order processPayment(Long orderId, Long userId) {
    // DB 작업
    Order order = orderRepository.save(...);
    
    // 트랜잭션 커밋 후 실행
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducerService.publishEvent(order);
            }
        }
    );
    
    return order;
}
```

### 6.2 멱등성 보장
**WHY?**
- 네트워크 장애로 같은 메시지가 여러 번 전송될 수 있음
- Consumer가 같은 메시지를 여러 번 처리하면 안 됨

**HOW?**
```java
// Producer: 멱등성 활성화
configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

// Consumer: 고유 ID로 중복 체크
@KafkaListener(topics = "coupon-issue-request")
public void consume(CouponIssueRequestEvent event) {
    String requestId = event.getRequestId(); // 고유 ID
    
    // Redis나 DB에서 이미 처리했는지 확인
    if (alreadyProcessed(requestId)) {
        log.info("이미 처리된 요청: {}", requestId);
        return;
    }
    
    // 실제 처리
    couponService.issue(event);
    
    // 처리 완료 기록
    markAsProcessed(requestId);
}
```
---

## 7. 주의사항 및 Best Practices

### ✅ DO
1. **Key를 잘 설계하라**
   - 같은 Key는 같은 Partition으로 가므로 순서 보장
   - 예: 주문 ID를 Key로 하면 같은 주문의 이벤트는 순서 보장

2. **Partition 수를 적절히 설정하라**
   - Consumer 수 ≤ Partition 수
   - 너무 많으면 관리 복잡, 너무 적으면 병렬 처리 제한

3. **Consumer에서 예외 처리를 철저히 하라**
   - 예외 발생 시 메시지 재처리 또는 Dead Letter Queue로 이동

4. **멱등성을 보장하라**
   - 같은 메시지를 여러 번 처리해도 결과가 같도록

---

## 8. 로컬 테스트 방법

### 8.1 Kafka 로컬 실행 (Windows)
```bash
# 1. Zookeeper 실행
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# 2. Kafka 서버 실행 (새 터미널)
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### 8.2 application.yml 설정
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: ecommerce-group
      auto-offset-reset: earliest
    producer:
      acks: all
```

### 8.3 테스트 시나리오
1. **주문 생성 → 결제 완료**
   - `POST /api/payment/{orderId}`
   - 콘솔에서 "주문 완료 이벤트 발행 완료" 로그 확인
   
2. **Consumer 로그 확인**
   - "Data Platform: Order completed event received"
   - "Notification Service: Order completed event received"

---

## 10. 결론

Kafka는 **대용량 실시간 데이터를 안정적으로 처리**하기 위한 강력한 도구입니다.

**우리 프로젝트에서 Kafka를 사용한 이유:**
1. ✅ 주문 완료 이벤트를 여러 시스템에서 독립적으로 소비
2. ✅ 쿠폰 발급 요청을 순차적으로 안전하게 처리
3. ✅ 시스템 간 느슨한 결합 (Loosely Coupled)
4. ✅ 메시지 보존으로 재처리 가능
