# E-Commerce Platform (HH Plus)

대용량 트래픽 처리를 고려한 E-Commerce 플랫폼입니다.
선착순 쿠폰 발급, 포인트 결제, 재고 관리 등의 핵심 기능을 제공합니다.

## 📋 프로젝트 개요

- **프로젝트명**: HH Plus E-Commerce
- **버전**: 0.0.1-SNAPSHOT
- **개발 언어**: Java 17
- **프레임워크**: Spring Boot 3.5.7

## 🏗️ 아키텍처

본 프로젝트는 Clean Architecture 원칙을 따라 계층을 분리했습니다:

```
src/main/java/com/commerce/hhplus_e_commerce/
├── controller/       # API 엔드포인트 정의
├── useCase/          # 비즈니스 유스케이스 오케스트레이션
├── service/          # 도메인 서비스 로직
├── domain/           # 도메인 엔티티 및 비즈니스 규칙
│   └── enums/        # 도메인 열거형
├── repository/       # 데이터 액세스 인터페이스
│   ├── impl/         # Repository 구현체
│   └── jpa/          # JPA Repository
└── dto/              # 데이터 전송 객체
```

## ✨ 주요 기능

### 1. 선착순 쿠폰 발급
- 제한된 수량의 쿠폰을 선착순으로 발급
- 동시성 제어를 통한 안전한 수량 관리
- 쿠폰 유효성 검증 및 중복 발급 방지

### 2. 주문 및 결제
- 장바구니 기반 주문 생성
- 포인트와 쿠폰을 활용한 할인 결제
- 재고 확인 및 자동 차감

### 3. 상품 관리
- 상품 조회 및 재고 관리
- 실시간 재고 업데이트

### 4. 포인트 시스템
- 사용자 포인트 충전 및 사용
- 포인트 히스토리 관리

## 🔧 기술 스택

### Backend
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Web

### Database
- MySQL / PostgreSQL / Oracle (다중 DB 지원)
- H2 (테스트용)

### Documentation
- Swagger/OpenAPI 3.0

### Build Tool
- Gradle 8.14.3

### Others
- Lombok
- JUnit 5

## 🚀 시작하기

### 사전 요구사항
- JDK 17 이상
- Gradle 8.x
- MySQL 8.x (또는 PostgreSQL, Oracle)

### 설치 및 실행

1. 프로젝트 클론
```bash
git clone [repository-url]
cd hhplus_e_commerce
```

2. 데이터베이스 설정
`src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 설정합니다.

3. 빌드 및 실행
```bash
./gradlew build
./gradlew bootRun
```

4. API 문서 확인
애플리케이션 실행 후 아래 주소에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

## 📚 API 문서

### 주요 엔드포인트

#### 쿠폰 API
- `PATCH /api/coupon/{couponId}/issue` - 선착순 쿠폰 발급

#### 주문 API
- `POST /api/orders` - 주문 생성 (PENDING 상태)
- `POST /api/orders/{orderId}/payment` - 결제 처리 (포인트 + 쿠폰 적용)

자세한 API 명세는 `/docs/openapi.yaml` 파일 또는 Swagger UI를 참고하세요.

## 🔐 동시성 제어 전략

### 쿠폰 선착순 발급

쿠폰 발급 기능은 다수의 사용자가 동시에 요청할 수 있으며, 쿠폰 수량은 제한되어 있습니다.
따라서 **"먼저 요청한 사용자가 먼저 쿠폰을 받는 것"**을 보장해야 합니다.

#### 구현 방식: `ReentrantLock(true)` 공정 락(Fair Lock)

```java
private final ReentrantLock lock = new ReentrantLock(true);

public void issueCoupon(String couponId, String userId) {
    lock.lock();
    try {
        // 쿠폰 발급 로직
    } finally {
        lock.unlock();
    }
}
```

#### 선택 이유
- **공정성 보장**: 공정 락은 락 획득 대기 큐를 유지하며, 먼저 락을 요청한 스레드가 먼저 락을 획득합니다
- **순서 보장**: 선착순 로직에서 발생할 수 있는 "나중에 요청한 유저가 먼저 쿠폰을 받는 문제"를 방지합니다
- **대안 대비 우위**: `synchronized` 또는 기본 비공정 락은 락 획득 순서를 보장하지 않기 때문에 선착순 보장이 불가능합니다

## 📊 데이터베이스 설계

ERD 및 시퀀스 다이어그램은 `docs/` 디렉토리에서 확인할 수 있습니다:

- `E_Commerce_ERD.png` - 데이터베이스 ERD
- `Coupon_Issuance_Sequence_Diagram.png` - 쿠폰 발급 시퀀스
- `order_request_sequence_diagram.png` - 주문 생성 시퀀스
- `product_payment_sequence_diagram.png` - 결제 처리 시퀀스
- `FlowChart-v0.1.png` - 전체 흐름도

## 🧪 테스트

테스트 실행:
```bash
./gradlew test
```

## 📝 라이선스

This project is licensed under the terms described in the project.

## 👥 기여

이슈 및 풀 리퀘스트는 언제든 환영합니다.
풀 리퀘스트 작성 시 `.github/pull_request_template.md.txt`를 참고해주세요.

## 📞 문의

프로젝트 관련 문의사항은 이슈를 통해 남겨주세요.
