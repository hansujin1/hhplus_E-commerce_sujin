# Redis를 이용한 분산 락

## 📌 분산 락 개요

분산 환경에서 여러 서버가 동시에 같은 리소스에 접근할 때, 데이터 정합성을 보장하기 위해 분산 락을 사용합니다.
Redis를 이용한 분산 락 구현 방식에는 크게 세 가지가 있습니다.

---

## 🔐 분산 락 종류 비교

### 1. Simple Lock (단순 락)

**특징:**
- 락 획득을 **한 번만** 시도
- 실패 시 즉시 예외 발생 (재시도 없음)
- 구현이 단순하고 Redis 부하가 가장 적음

**동작 방식:**
```
tryLock() → 실패 → 즉시 예외 발생
```

**장점:**
- 구현이 매우 간단
- Redis 요청 1회만 발생 (부하 최소)
- 빠른 실패 처리 (Fail-Fast)

**단점:**
- 재시도 없어서 락 획득 실패율 높음
- 사용자에게 "다시 시도하세요" 메시지만 제공

**적용 사례:**
- 경합이 거의 없는 로직
- 중복 요청 방지 (예: 중복 주문 방지)
- 실패해도 큰 문제가 없는 작업

---

### 2. Spin Lock (스핀 락)

**특징:**
- 락을 **획득할 때까지 반복 시도** (폴링 방식)
- 일정 간격(예: 100ms)으로 sleep 후 재시도
- 횟수 또는 시간 제한 설정 가능

**동작 방식:**
```
tryLock() → 실패 → 100ms sleep → 재시도 → 실패 → sleep → ...
```

**장점:**
- 자동 재시도로 락 획득 가능성 높음
- 구현이 비교적 간단
- 타임아웃 설정으로 무한 대기 방지

**단점:**
- 계속해서 Redis에 **요청**하기 때문에 Redis 부하 높음
- 네트워크 트래픽 증가
- Sleep으로 인한 응답 지연

**적용 사례:**
- 락 점유 시간이 매우 짧은 경우 (밀리초 단위)
- 중간 수준의 트래픽
- 구현 복잡도를 낮추고 싶을 때

---

### 3. Pub/Sub Lock (발행-구독 락)

**특징:**
- 락 해제 시 **알림(이벤트)** 받아서 재시도
- Redisson 라이브러리가 내부적으로 구현
- 불필요한 폴링 없이 효율적인 대기

**동작 방식:**
```
tryLock() → 실패 → 채널 구독 → 대기 → unlock 알림 받음 → 재시도
```

**장점:**
- Redis 부하 최소 (이벤트 기반)
- 네트워크 효율적 (불필요한 폴링 없음)
- 높은 트래픽에서도 안정적
- 락 해제 시 즉시 반응

**단점:**
- 구현 복잡도 높음 (Redisson 사용 권장)
- **순서를 보장하지 않음** (먼저 요청 ≠ 먼저 처리)

**적용 사례:**
- 트래픽이 많고 경합이 심한 경우
- 선착순 이벤트, 한정 수량 판매
- 프로덕션 환경의 핵심 기능

---

## 📊 비교표

| 구분 | 재시도 | Redis 부하 | 응답 속도 | 구현 난이도 | 순서 보장 |
|------|--------|-----------|----------|-----------|----------|
| **Simple Lock** | ❌ 없음 | 낮음 (1회) | 매우 빠름 | 쉬움 | ❌ |
| **Spin Lock** | ✅ 있음 (폴링) | 높음 (지속적) | 느림 (sleep) | 쉬움 | ❌ |
| **Pub/Sub Lock** | ✅ 있음 (이벤트) | 낮음 (이벤트) | 빠름 | 어려움 | ❌ |

> ⚠️ **중요:** 세 가지 방식 모두 **도착 순서를 보장하지 않습니다.**  
> 진짜 선착순이 필요한 경우 Redis Sorted Set 또는 Queue를 사용해야 합니다.

---

## 🎯 프로젝트 적용 전략

### 1. 쿠폰 발급 (선착순 이벤트)

**선택:** Pub/Sub Lock (Redisson)

**이유:**
- 100명이 동시에 10개 쿠폰 신청 → 경합 매우 심함
- 자동 재시도 필요
- Redis 부하 최소화 필요

**구현:**
```java
@DistributedLock(
    key = "'coupon:issue:' + #couponId",
    waitTime = 5L,
    leaseTime = 3L
)
public UserCoupon issueCoupon(Long userId, Long couponId) {
    // 쿠폰 발급 로직
}
```

**참고:**
- 순서는 보장되지 않지만, 정확히 10개만 발급되는 것이 중요


---

### 2. 재고 차감

**선택:** Pub/Sub Lock (Redisson)

**이유:**
- 여러 사용자가 동시에 같은 상품 주문
- 재고 정합성이 핵심
- 높은 트래픽 대비

**구현:**
```java
@DistributedLock(
    key = "'product:stock:' + #productId",
    waitTime = 5L,
    leaseTime = 3L
)
public void decreaseStock(Long productId, int quantity) {
    // 재고 차감 로직
}
```

---

### 3. 포인트 사용

**선택:** Simple Lock 또는 낙관적 락

**이유:**
- 같은 사용자가 동시 요청할 확률 매우 낮음
- 경합이 거의 없음
- 단순한 구현으로 충분

**방안 A: Simple Lock**
```java
@DistributedLock(
    key = "'point:user:' + #userId",
    waitTime = 0L  // 재시도 없음
)
public void usePoint(Long userId, int amount) {
    // 포인트 차감 로직
}
```

**방안 B: 낙관적 락 (JPA @Version)**
```java
@Entity
public class User {
    @Version
    private Long version;
    
    private int point;
}

@Transactional
public void usePoint(Long userId, int amount) {
    User user = userRepository.findById(userId).orElseThrow();
    user.usePoint(amount);
    // 커밋 시 version 자동 체크
}
```

---

### 4. 주문 생성

**분석:**
주문 프로세스는 여러 단계로 구성됩니다.
```
주문 생성 → 재고 차감 → 쿠폰 사용 → 포인트 차감 → 결제 처리
```

**전략 A: 리소스별 개별 락 (권장)**
```java
@Transactional
public Order createOrder(Long userId, OrderRequest request) {
    // 1. 주문 생성 (락 없음)
    Order order = new Order(userId);
    
    // 2. 재고 차감 (상품별 Pub/Sub Lock)
    for (OrderItem item : request.getItems()) {
        productFacade.decreaseStock(item.getProductId(), item.getQuantity());
    }
    
    // 3. 쿠폰 사용 (쿠폰별 Lock)
    if (request.getCouponId() != null) {
        couponFacade.useCoupon(userId, request.getCouponId());
    }
    
    // 4. 포인트 차감 (사용자별 Lock)
    if (request.getUsePoint() > 0) {
        pointFacade.usePoint(userId, request.getUsePoint());
    }
    
    return orderRepository.save(order);
}
```

**전략 B: 주문 전체에 사용자별 락**
```java
@DistributedLock(key = "'order:user:' + #userId")
public Order createOrder(Long userId, OrderRequest request) {
    // 전체 주문 프로세스
}
```

**권장:** 전략 A (리소스별 개별 락)
- 세밀한 동시성 제어
- 다른 사용자는 동시 주문 가능
- 성능 우수

---

## 🔍 순서 보장 이슈

### 문제점
Simple Lock, Spin Lock, Pub/Sub Lock 모두 **먼저 요청한 순서대로 처리되지 않습니다.**

**이유:**
- Spin Lock: Sleep 타이밍, 네트워크 지연에 따라 순서 변경
- Pub/Sub: 락 해제 시 모든 대기자가 동시에 알림 받고 다시 경쟁

### 진짜 선착순이 필요한 경우

**Redis Sorted Set 사용:**
```java
// 타임스탬프로 순서 관리
redisTemplate.opsForZSet().add(queueKey, userId, System.currentTimeMillis());

// 순위 확인
Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);

// 1등부터 순서대로 처리
```

**적용 사례:**
- 티켓 예매 (콘서트, 기차 등)
- 아파트 청약
- 법적 공정성이 중요한 경우

**일반 쿠폰 이벤트는 순서 보장 불필요:**
- "선착순"은 마케팅 용어
- 실제로는 "빨리 처리된 순"
- 수량 제한만 정확하면 충분

---

## 📝 요약

### 이 프로젝트의 분산 락 적용

| 적용 위치 | 락 방식 | 키 기준 | 이유 |
|---------|---------|---------|------|
| 쿠폰 발급 (CouponFacade) | Pub/Sub (Redisson) | couponId | 경합 심함, 선착순 |
| 재고 차감 (ProductFacade) | Pub/Sub (Redisson) | productId | 여러 사용자 동시 주문 |
| 포인트 차감 (PaymentFacade) | Simple Lock | userId | 경합 낮음, 즉시 실패 OK |


### 핵심 원칙

1. **경합이 심한 곳**: Pub/Sub Lock (Redisson)
   - 쿠폰 발급, 재고 차감
   
2. **경합이 적은 곳**: Simple Lock 또는 낙관적 락
   - 포인트 사용
   
3. **리소스별 개별 락**: 
   - 주문 UseCase는 여러 리소스를 사용하지만, 각 리소스에만 락 적용
   - 다른 사용자의 동시 주문 가능
   
4. **순서가 중요한 경우**: Sorted Set/Queue 사용
   - 현재 프로젝트에는 미적용 (수량 제어만 중요)

### 보장 사항

✅ 정확한 수량 제어 (쿠폰 10개, 재고 100개)  
✅ 중복 발급/차감 방지  
✅ 동시성 안전성  
✅ 높은 처리량 (리소스별 개별 락)  
❌ 도착 순서대로 처리 (보장 안 함)