# 예시 — refactor (리팩토링)

## 예시 1: 서비스 로직 개선

```
refactor: #DBINS-1010 PremiumDiscountService 등급 산정 로직 개선

- fall-through 조건을 GradeCode enum으로 캡슐화
- 하드코딩된 할인율을 TB_DISCOUNT_POLICY 조회로 대체
```

## 예시 2: 메서드 분리

```
refactor: #DBINS-1055 ClaimServiceImpl 메서드 길이 15줄 이내로 분리

- registerClaim을 fetchPolicyOrThrow·buildInsertParam·notifyApproval로 분리
- 단일 책임 원칙 준수를 위해 알림 발송 로직 별도 메서드로 추출
```
