# 예시 — feat (기능 추가)

## 예시 1: 신규 API 엔드포인트 추가

```
feat: #DBINS-1001 보험 클레임 접수 API 구현

- ClaimController에 POST /api/v1/claims 엔드포인트 추가
- 중복 클레임 방지 검증 로직 구현
- 클레임 접수 시 고객 등급·할인율 스냅샷 저장
```

## 예시 2: 할인율 계산 로직 실제 구현

```
feat: #DBINS-1001 보험료 할인율 실제 계산 로직 구현

- GradeCode enum 추가: VIP→GOLD→SILVER→BASIC fall-through 조건 캡슐화
- PremiumDiscountServiceImpl 구현: TB_DISCOUNT_POLICY 조회 기반 할인율 계산
- PremiumMapper 및 repository Row 클래스 추가
- PremiumDiscountServiceStub 삭제: 임시 구현체를 실제 구현체로 대체
```
