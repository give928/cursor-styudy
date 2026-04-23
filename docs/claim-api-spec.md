# 보험 클레임 접수 API 명세서

> **작성일:** 2026-04-23
> **버전:** v1
> **작성자:** DB Inc. 백엔드 개발팀
> **Base URL:** `http://localhost:8080`

---

## 공통 응답 구조

모든 API는 아래 공통 래퍼로 응답합니다.

### 성공

```json
{
  "success": true,
  "data": { },
  "timestamp": "2026-04-23T06:30:00.000Z"
}
```

### 실패

```json
{
  "success": false,
  "errorCode": "CLAIM_001",
  "message": "클레임을 찾을 수 없습니다.",
  "timestamp": "2026-04-23T06:30:00.000Z"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | Boolean | 요청 성공 여부 |
| `data` | Object | 성공 시 본문 데이터 (실패 시 미포함) |
| `errorCode` | String | 실패 시 에러 코드 (성공 시 미포함) |
| `message` | String | 실패 시 사용자 메시지 (성공 시 미포함) |
| `timestamp` | String | 응답 시각 (ISO-8601) |

---

## API 목록

---

### 1. 클레임 접수

#### 기본 정보

| 항목 | 내용 |
|------|------|
| **HTTP 메서드** | `POST` |
| **엔드포인트** | `/api/v1/claims` |
| **Content-Type** | `application/json` |
| **설명** | 보험 계약에 대한 클레임(보험금 청구)을 접수합니다. 접수 시 고객 등급(`gradeCd`) 기반 할인율이 자동으로 계산되어 함께 저장됩니다. |

#### 요청 파라미터 (Request Body)

| 필드 | 타입 | 필수 | 제약 | 설명 |
|------|------|:----:|------|------|
| `policySeq` | Long | **Y** | 양수 | 보험 계약 PK |
| `claimType` | String | **Y** | 공백 불가 | 클레임 유형 (예: `ACCIDENT`, `DISEASE`) |
| `accidentDtm` | String | **Y** | ISO-8601 형식 | 사고 발생 일시 (예: `2026-04-20T14:30:00`) |
| `claimAmt` | Number | **Y** | 양수, 소수점 2자리 | 청구 금액 (원 단위) |
| `actorId` | String | N | — | 처리자 ID. 미입력 시 `SYSTEM` 으로 저장 |

> **주의:** `claimAmt`는 부동소수점 오차를 방지하기 위해 **문자열이 아닌 숫자(Number)** 로 전송해야 합니다.

#### 응답 데이터 구조 (`data`)

| 필드 | 타입 | 설명 |
|------|------|------|
| `claimSeq` | Long | 클레임 PK |
| `claimNo` | String | 클레임 번호 (예: `CLM000000005000`) |
| `policySeq` | Long | 연결된 보험 계약 PK |
| `claimType` | String | 클레임 유형 |
| `statusCd` | String | 클레임 상태. 접수 직후 `RECEIPT` 고정 |
| `accidentDtm` | String | 사고 발생 일시 (ISO-8601) |
| `receiptDtm` | String | 클레임 접수 일시 (ISO-8601) |
| `claimAmt` | Number | 청구 금액 |
| `approvedAmt` | Number | 승인 금액 (접수 시 `0`) |
| `discountRate` | Number | 적용 할인율 (예: `0.05` = 5%) |
| `gradeCd` | String | 접수 시점 고객 등급 (`BASIC` / `SILVER` / `GOLD` / `VIP`) |
| `rejectReason` | String | 거절 사유 (접수·승인 상태에서는 `null`) |

#### 상태 코드

| HTTP 상태 | 에러 코드 | 발생 조건 |
|:---------:|-----------|-----------|
| `200` | — | 정상 접수 |
| `400` | `CLAIM_002` | 요청 필드 유효성 검사 실패 |
| `400` | `POLICY_001` | 존재하지 않는 `policySeq` |
| `500` | `CLAIM_099` | 서버 내부 오류 |

#### cURL 예제

```bash
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "policySeq": 10000,
    "claimType": "ACCIDENT",
    "accidentDtm": "2026-04-20T14:30:00",
    "claimAmt": 500000.00,
    "actorId": "user01"
  }'
```

#### 응답 예제 (성공)

```json
{
  "success": true,
  "data": {
    "claimSeq": 5000,
    "claimNo": "CLM000000005000",
    "policySeq": 10000,
    "claimType": "ACCIDENT",
    "statusCd": "RECEIPT",
    "accidentDtm": "2026-04-20T14:30:00",
    "receiptDtm": "2026-04-23T06:30:00",
    "claimAmt": 500000.00,
    "approvedAmt": 0,
    "discountRate": 0.05,
    "gradeCd": "GOLD",
    "rejectReason": null
  },
  "timestamp": "2026-04-23T06:30:00.123Z"
}
```

#### 응답 예제 (실패 — 존재하지 않는 계약)

```json
{
  "success": false,
  "errorCode": "POLICY_001",
  "message": "존재하지 않는 계약입니다. policySeq=99999",
  "timestamp": "2026-04-23T06:30:00.456Z"
}
```

---

### 2. 클레임 단건 조회

#### 기본 정보

| 항목 | 내용 |
|------|------|
| **HTTP 메서드** | `GET` |
| **엔드포인트** | `/api/v1/claims/{claimSeq}` |
| **설명** | 클레임 PK로 단건을 조회합니다. |

#### Path 파라미터

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|:----:|------|
| `claimSeq` | Long | **Y** | 조회할 클레임 PK |

#### 응답 데이터 구조

클레임 접수 API의 `data` 구조와 동일합니다.

#### 상태 코드

| HTTP 상태 | 에러 코드 | 발생 조건 |
|:---------:|-----------|-----------|
| `200` | — | 정상 조회 |
| `404` | `CLAIM_001` | 존재하지 않는 `claimSeq` |
| `500` | `CLAIM_099` | 서버 내부 오류 |

#### cURL 예제

```bash
curl -X GET http://localhost:8080/api/v1/claims/5000
```

---

## 클레임 상태 흐름

```
RECEIPT(접수) ──▶ APPROVED(승인)
               └─▶ REJECTED(거절)
```

| 상태 코드 | 설명 |
|-----------|------|
| `RECEIPT` | 접수 완료. 심사 대기 중 |
| `APPROVED` | 심사 승인. 이메일·SMS 알림 자동 발송 |
| `REJECTED` | 심사 거절. `rejectReason` 필드에 사유 기재 |

---

## 고객 등급 및 할인율 참고

| 등급 | 할인율 | 조건 |
|------|:------:|------|
| `VIP` | 10% | 가입 5년 이상 + 무클레임 + 연보험료 1,000만원 이상 |
| `GOLD` | 5% | 가입 3년 이상 + 무클레임 |
| `SILVER` | 3% | 가입 1년 이상 |
| `BASIC` | 0% | 기본값 |

> 할인율은 **접수 시점의 등급을 스냅샷으로 저장**하며, 이후 등급 변경이 발생해도 해당 클레임의 할인율은 변경되지 않습니다.

---

*문의: 백엔드 개발팀 채널 `#backend-api`*
