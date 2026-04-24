---
name: git-commit
description: 깃 커밋 컨벤션에 맞춰 커밋 메시지를 생성한다. 사용자가 커밋 메시지 작성을 요청하거나, git commit, 커밋 컨벤션, 변경사항 정리를 언급할 때 사용한다. Generates git commit messages following the project's commit convention with type prefix, issue number, summary, and bullet-point changes.
---

# Git Commit

## 커밋 메시지 형식

```
{타입}: {이슈번호} {한 줄 요약}

- {변경사항 1}
- {변경사항 2}
...
```

## 절차

1. `git diff --staged` 또는 `git status`로 변경사항을 파악한다.
2. 변경의 성격에 맞는 타입을 선택한다.
3. 이슈번호가 있으면 포함하고, 없으면 생략한다.
4. 한 줄 요약은 명령형 현재 시제 한국어로 작성한다.
5. 변경사항 목록은 무엇을 왜 했는지 간결하게 서술한다.
6. 커밋을 직접 실행하지 않고 아래 형식으로 메시지만 출력한다.

```
{타입}: {이슈번호} {한 줄 요약}

- {변경사항 1}
- {변경사항 2}
```

## 타입 목록

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `design` | CSS 등 사용자 UI 디자인 변경 |
| `!BREAKING CHANGE` | 커다란 API 변경 |
| `!HOTFIX` | 급하게 치명적인 버그를 수정 |
| `style` | 코드 포맷 변경, 세미콜론 누락 등 코드 수정 없음 |
| `refactor` | 프로덕션 코드 리팩토링 |
| `comment` | 필요한 주석 추가 및 변경 |
| `docs` | 문서 수정 |
| `test` | 테스트 코드 추가 및 리팩토링, 프로덕션 코드 변경 없음 |
| `chore` | 빌드 업무 수정, 패키지 매니저 수정 등 프로덕션 코드 변경 없음 |
| `rename` | 파일 또는 폴더명 수정 및 이동 |
| `remove` | 파일 삭제 |

## 예시

**예시 1 — 기능 추가:**
```
feat: #DBINS-1001 보험 클레임 접수 API 구현

- ClaimController에 POST /api/v1/claims 엔드포인트 추가
- 중복 클레임 방지 검증 로직 구현
- 클레임 접수 시 고객 등급·할인율 스냅샷 저장
```

**예시 2 — 버그 수정:**
```
fix: #DBINS-1023 클레임 승인 시 알림 미발송 문제 수정

- ClaimServiceImpl의 트랜잭션 경계 오류 수정
- TB_NOTIFICATION INSERT 누락 로직 보완
```

**예시 3 — 리팩토링:**
```
refactor: #DBINS-1010 PremiumDiscountService 등급 산정 로직 개선

- fall-through 조건을 GradeCode enum으로 캡슐화
- 하드코딩된 할인율을 TB_DISCOUNT_POLICY 조회로 대체
```
