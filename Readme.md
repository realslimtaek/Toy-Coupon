# 🚀 Toy-Coupon: 고성능 분산 쿠폰 발급 시스템

본 프로젝트는 초당 수만 건 이상의 트래픽이 몰리는 선착순 쿠폰 발급 상황을 가정하여, **Redis**와 **RabbitMQ**를 활용해 데이터 정합성을 보장하고 시스템 부하를 분산시킨 비동기 쿠폰 발급 시스템입니다.

## 🏗 System Architecture
시스템은 크게 요청을 처리하는 **Producer(Toy-Coupon)**와 실제 DB 반영을 담당하는 **Consumer(Toy-Issue-Coupon)**로 분리되어 있습니다.

1.  **Request (WebFlux):** 비동기 논블로킹 방식으로 대량의 사용자 요청을 수용합니다.
2.  **Validation (Redis):** `SADD` 명령어를 통해 1인 1매 중복 발급을 원자적으로 차단하고, `AtomicLong`으로 실시간 재고를 관리합니다.
3.  **Messaging (RabbitMQ):** 검증된 요청은 메시지 큐에 적재되어 부하를 평탄화(Load Leveling)합니다.
4.  **Persistence (R2DBC):** Consumer가 큐에서 메시지를 가져와 DB 재고를 차감하고 발급 이력을 저장합니다. (Atomic Update 활용)

---

## ✨ Key Features & Technical Challenges

### 1. 동시성 제어 및 중복 발급 방지 (Race Condition 해결)
*   **문제:** 단순 조회 후 저장(Check-then-Act) 방식은 멀티스레드 환경에서 1인 다수 발급의 위험이 있음.
*   **해결:** Redis의 `SADD` 명령어를 사용하여 **조회와 동시에 마킹**을 수행하는 원자적 연산으로 1인 1매 제한을 완벽히 보장합니다.

### 2. Redis 기반 실시간 재고 관리
*   **문제:** DB 직접 접근은 병목 현상의 원인이 됨.
*   **해결:** Redis `decrementAndGet`을 사용하여 인메모리에서 초고속으로 재고를 차감합니다. 재고 부족 시 즉시 실패 응답을 보내어 불필요한 IO를 차단합니다.

### 3. 메시지 큐를 활용한 부하 분산 (Asynchronous Processing)
*   **문제:** 이벤트 오픈 시 DB 쓰기 부하가 집중되어 전체 시스템 장애 발생 가능성.
*   **해결:** RabbitMQ를 도입하여 Producer는 검증만 수행하고 응답을 즉시 반환하며, Consumer가 가용 리소스에 맞춰 순차적으로 DB에 반영합니다.

### 4. 데이터 최종 정합성 보장 (Eventual Consistency)
*   **Atomic DB Update:** `UPDATE ... SET REMAIN = REMAIN - 1 WHERE id = :id AND REMAIN > 0` 쿼리를 통해 DB 레벨에서도 초과 발급을 2중으로 방어합니다.
*   **Idempotency:** DB의 `(coupon_id, user_id)` 유니크 제약 조건을 통해 메시지 재전송 상황에서도 데이터 중복을 방지합니다.

---

## 🛠 Tech Stack
*   **Language:** Kotlin
*   **Framework:** Spring Boot 4.x, Spring WebFlux
*   **Database:** R2DBC (MySQL), Redis (Redisson)
*   **Message Broker:** RabbitMQ
*   **Build Tool:** Gradle

---

## 🚦 Getting Started

### Prerequisites
*   Java 21+
*   Docker (Redis, RabbitMQ 실행용)

### Setup & Run
```bash
# 1. 인프라 실행
docker-compose up -d

# 2. Producer 실행 (Port: 8080)
./gradlew :Toy-Coupon:bootRun

# 3. Consumer 실행 (Port: 8081)
./gradlew :Toy-Issue-Coupon:bootRun
```

### API Test (High Concurrency)
10명의 사용자가 동시에 요청을 보내는 API 시나리오를 테스트할 수 있습니다.
```bash
POST /coupon/issue-test?userId=22&couponId=1
```
해당 내용에서 userId는 임의로 들어가기때문에 어떤 값을 입력해도 상관 없습니다.
---

## 📈 성능 테스트 및 검증 로그
*   **중복 체크 검증:** 동일 유저의 10번 동시 호출 시, Redis `SADD`를 통해 오직 **1개의 메시지만 발행**됨을 확인.
*   **재고 소진 테스트:** 설정된 재고 수량만큼만 메시지가 발행되고, 이후 요청은 즉시 `이미 발급 만료된 쿠폰입니다` 에러가 반환됨을 확인.
