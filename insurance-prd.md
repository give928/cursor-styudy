# [교육 자료] insurance-prd.md — 보험 계약 관리 시스템 PRD
---

## 1. 서비스 개요

### 1.1 배경

DB손해보험 계열사는 보험 계약 체결부터 클레임 처리, 납입 관리, 만기 갱신까지 전 과정을 처리하는 백엔드 시스템이 필요합니다. 현재 Oracle Forms 기반의 레거시 시스템을 Spring Boot + MyBatis 기반으로 현대화합니다.

### 1.2 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 2.7, MyBatis 3.5
- **데이터베이스**: Oracle 19c
- **아키텍처**: Layered Architecture (Controller → Service → Repository → DB)

---

## 2. 도메인 용어 정의

| 용어 | 영문 | 설명 |
| --- | --- | --- |
| 계약자 | Contractor | 보험료를 납입하는 사람 (법인 가능) |
| 피보험자 | Insured | 보험의 대상이 되는 사람 |
| 수익자 | Beneficiary | 보험금을 받는 사람 |
| 보험증권 | Policy | 계약의 증거 문서. 고유 POLICY_NO 존재 |
| 클레임 | Claim | 보험금 청구. 사고 발생 후 접수 |
| 납입주기 | Payment Cycle | M(월납)/Q(분기납)/S(반납)/Y(연납) |
| 고객등급 | Grade | BASIC/SILVER/GOLD/VIP |
| 설계사 | Agent | 보험 계약을 중개하는 사람 |

---

## 3. 핵심 비즈니스 규칙

### 3.1 고객 등급 & 할인율

| 등급 | 할인율 | 가입 기간 | 무클레임 | 연 보험료 |
| --- | --- | --- | --- | --- |
| VIP | 10% | 5년 이상 | 필수 | 1,000만원 이상 |
| GOLD | 5% | 3년 이상 | 필수 | 조건 없음 |
| SILVER | 3% | 1년 이상 | 조건 없음 | 조건 없음 |
| BASIC | 0% | 기본값 | - | - |

**fall-through 규칙**: 상위 등급 조건 불충족 시 하위 등급 할인율 적용

### 3.2 클레임 처리 규칙

- 클레임 접수 시 즉시 `RECEIPT` 상태로 저장
- 심사 완료 후 `APPROVED` 또는 `REJECTED` 상태로 변경
- `APPROVED` 시 자동 알림 발송 (이메일 + SMS)
- 동일 계약에 처리 중인 클레임이 있으면 신규 접수 불가
- 클레임 접수 시 **고객 등급에 따른 할인율을 함께 기록** (이 교육의 관통 시나리오)

### 3.3 납입 관리 규칙

- 납입 기한 7일 전 자동 알림 발송
- 납입 기한 초과 시 계약 상태 `GRACE` (유예기간)
- 유예기간 30일 초과 시 계약 `LAPSED` (실효) 처리

### 3.4 계약 갱신 규칙

- 만기 30일 전 자동 갱신 안내 알림 발송 (배치)
- `AUTO_RENEW_YN = 'Y'`인 계약은 만기 당일 자동 갱신
- 갱신 시 등급 재산정 후 새 할인율 적용

---

## 4. 핵심 기능 목록

### 고객 관리 (Customer)

| 기능 ID | 기능명 | 우선순위 |
| --- | --- | --- |
| CUST-001 | 고객 등록 | P1 |
| CUST-002 | 고객 조회 | P1 |
| CUST-003 | 고객 등급 조회 | P1 |

### 계약 관리 (Policy)

| 기능 ID | 기능명 | 우선순위 |
| --- | --- | --- |
| POLY-001 | 계약 생성 | P1 |
| POLY-002 | 계약 조회 | P1 |
| POLY-003 | 계약 상태 변경 | P1 |
| POLY-004 | 만기 예정 계약 목록 (30일 내) | P1 |

### 클레임 처리 (Claim)

| 기능 ID | 기능명 | 우선순위 |
| --- | --- | --- |
| CLIM-001 | 클레임 접수 (할인율 자동 계산) | P1 |
| CLIM-002 | 클레임 조회 | P1 |
| CLIM-003 | 클레임 심사 (APPROVED/REJECTED) | P1 |

### 납입 관리 (Payment)

| 기능 ID | 기능명 | 우선순위 |
| --- | --- | --- |
| PAYM-001 | 납입 처리 | P1 |
| PAYM-002 | 납입 이력 조회 | P1 |
| PAYM-003 | 미납 계약 목록 | P1 |

---

## 5. 데이터 요구사항

### 테이블 목록 (11개)

| 테이블명 | 설명 | 핵심 컬럼 |
| --- | --- | --- |
| TB_CODE_MASTER | 공통 코드 | CODE_GRP, CODE_CD, CODE_NM |
| TB_AGENT | 설계사 | AGENT_SEQ, AGENT_NO, AGENT_NM |
| TB_CUSTOMER | 고객 | CUST_SEQ, CUST_NO, GRADE_CD |
| TB_PRODUCT | 보험 상품 | PROD_SEQ, PROD_CD, BASE_PREMIUM |
| TB_DISCOUNT_POLICY | 할인 정책 | GRADE_CD, DISCOUNT_RATE, MIN_CONTRACT_YEAR |
| TB_POLICY | 보험 계약 | POLICY_SEQ, POLICY_NO, STATUS_CD, EXPIRE_DTM |
| TB_CLAIM | 클레임 | CLAIM_SEQ, POLICY_SEQ, DISCOUNT_RATE, GRADE_CD |
| TB_CLAIM_DETAIL | 클레임 상세 | DETAIL_SEQ, CLAIM_SEQ, ITEM_AMT |
| TB_PAYMENT | 납입 이력 | PAYMENT_SEQ, POLICY_SEQ, DUE_DTM |
| TB_NOTIFICATION | 알림 이력 | NOTI_SEQ, NOTI_TYPE, SEND_STATUS |
| TB_AUDIT_LOG | 감사 로그 | LOG_SEQ, ACTION_CD, TARGET_TABLE, TARGET_SEQ |

### 공통 감사 컬럼 (모든 테이블 포함)

```sql
REG_DTM  DATE         NOT NULL  -- 등록일시 (DEFAULT SYSDATE)
REG_ID   VARCHAR2(50) NOT NULL  -- 등록자 ID
UPD_DTM  DATE         NOT NULL  -- 수정일시
UPD_ID   VARCHAR2(50) NOT NULL  -- 수정자 ID
```

---

## 6. API 설계 요구사항

### 공통 응답 형식

```json
// 성공
{ "success": true, "data": { ... }, "timestamp": "2026-04-19T10:00:00" }

// 실패
{ "success": false, "errorCode": "CLAIM_001", "message": "처리 중인 클레임이 존재합니다", "timestamp": "..." }
```

### 핵심 API 목록

```
[고객]
POST   /api/v1/customers              고객 등록
GET    /api/v1/customers/{custSeq}    고객 조회
GET    /api/v1/customers/{custSeq}/grade  등급 조회

[계약]
POST   /api/v1/policies              계약 생성
GET    /api/v1/policies/{policySeq}  계약 조회
PATCH  /api/v1/policies/{policySeq}/status  상태 변경
GET    /api/v1/policies/expiring     만기 예정 목록 (30일 내)

[클레임]
POST   /api/v1/claims                클레임 접수 (할인율 자동 계산)
GET    /api/v1/claims/{claimSeq}     클레임 조회
PATCH  /api/v1/claims/{claimSeq}/review  심사 처리

[납입]
POST   /api/v1/payments              납입 처리
GET    /api/v1/policies/{policySeq}/payments  납입 이력
```

---

## 7. Oracle 전용 규칙 (AI에 반드시 명시)

```sql
-- ✅ Oracle 전용 (반드시 사용)
SYSDATE                        -- 현재 일시 (MySQL NOW() 금지)
ADD_MONTHS(SYSDATE, -12)       -- 월 계산 (MySQL DATE_ADD() 금지)
ROW_NUMBER() OVER(ORDER BY ...) -- 페이징 (MySQL LIMIT 금지)
SEQ_[테이블약어].NEXTVAL        -- PK 자동 증가 (AUTO_INCREMENT 금지)
NVL(컬럼, 기본값)               -- NULL 처리 (MySQL IFNULL() 금지)

-- ❌ MySQL 문법 (절대 금지)
NOW(), DATE_ADD(), LIMIT, IFNULL, AUTO_INCREMENT
```