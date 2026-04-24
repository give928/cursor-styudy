# 예시 — fix (버그 수정)

## 예시 1: 알림 미발송 버그

```
fix: #DBINS-1023 클레임 승인 시 알림 미발송 문제 수정

- ClaimServiceImpl의 트랜잭션 경계 오류 수정
- TB_NOTIFICATION INSERT 누락 로직 보완
```

## 예시 2: MapperScan 범위 오류

```
fix: #DBINS-1045 MyBatis BindingException 발생 문제 해결

- @MapperScan 범위를 *.repository 패키지로 한정
- 상위 패키지 전체 스캔으로 Service가 Mapper로 오등록되던 문제 제거
```
