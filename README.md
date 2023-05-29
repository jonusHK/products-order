# 프로젝트 설명
* 상품 주문이 가능한 서비스

# 사용 언어 및 프레임 워크
* Kotlin 1.8.21 (Java 17)
* Spring Boot 3.1.0

# 외부 라이브러리
* Redisson
    * 목적 : 분산 락 구현
* Kotest & MockK
    * 목적 : 테스트 코드

# 프로젝트 구조
* App
    * 비즈니스 로직
    * Command : 주문 실행
    * Service : 주문, 결제, 상품
* Core
    * 핵심 모듈
    * Config : 임베디드 레디스 설정 파일
    * DTO : 장바구니, 주문, 주문 정보, 주문 상품, 상품, 결제
    * Entity : 주문, 주문 상품, 상품, 결제
    * Enum : 주문 상태, 결제 상태
    * Exception : 발생 가능한 예외
    * External : Redisson
    * Mapper : DTO -> Entity, Entity -> DTO 변환
    * Reader : 상품 리스트 파일 Reader
    * Repository : 주문, 주문 상품, 상품, 결제
    * Utils : Format 변환

# 모델
### 주문
* `Order`
* ID, 총 금액(totalPrice), 주문 상태(status), 주문 일시(created)

### 주문 상품
* `OrderProduct`
* 주문 ID(orderId), 상품 ID(productId), 수량(quantity), 가격(price)
* 주문 : 주문 상품 = 1 : N
* 상품 : 주문 상품 = 1 : N

### 상품
* `Product`
* ID, 상품번호(number), 상품명(name), 가격(price), 재고수량(quantity)

### 결제
* `Payment`
* 주문 ID(orderId), 상품 금액(price), 배송료(deliveryFee), 총 결제금액(totalFee), 결제 상태(status), 결제 일시(created)
* 주문 : 결제 = 1 : 1

# 플로우
### 주문
1. 주문, 결제, 상품 정보 초기화
2. 주문 시작 (`o 또는 order 입력`)
<img src="https://github.com/jonusHK/products-order/assets/48202867/8ab4120f-d1f2-4ace-b9f2-be278384f364" />
3. 장바구니에 상품 추가 (`상품번호 입력 + ENTER`, `수량 입력 + ENTER`)
<img src="https://github.com/jonusHK/products-order/assets/48202867/13bac112-f5d6-431d-ac9b-b2ff9dbdf658" />
4. 장바구니 상품으로 주문 및 결제 (`상품번호 또는 수량에 SPACE + ENTER`)
<img src="https://github.com/jonusHK/products-order/assets/48202867/495a87fc-192c-4980-add8-2e8ab53a207f" />
5. 재고 부족한 경우 `SoldOutException` 발생
<img src="https://github.com/jonusHK/products-order/assets/48202867/4a6b4877-e06d-4867-97bb-0ec59aad6780" />

### 취소
1. 주문 종료 메시지 출력 (`q 또는 quit 입력`)
<img src="https://github.com/jonusHK/products-order/assets/48202867/2e4f3c0b-08e3-452a-ab8e-23a11a9edd5f" />
2. 어플리케이션 종료
