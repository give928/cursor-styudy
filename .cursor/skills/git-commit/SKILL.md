---
name: git-commit
description: 깃 커밋 컨벤션에 맞춰 커밋 메시지를 생성한다. 사용자가 커밋 메시지 작성을 요청하거나, git commit, 커밋 컨벤션, 변경사항 정리를 언급할 때 사용한다. Generates git commit messages following the project's commit convention with type prefix, issue number, summary, and bullet-point changes.
---

# Git Commit Skill

## 작업 절차

1. `git diff --staged` 또는 `git status`로 변경사항을 파악한다.
2. 변경의 성격에 맞는 타입을 [references/api.md](references/api.md)에서 선택한다.
3. 이슈번호가 있으면 포함하고, 없으면 생략한다.
4. 한 줄 요약은 명령형 현재 시제 한국어로 작성한다.
5. 변경사항 목록은 무엇을 왜 했는지 간결하게 서술한다.
6. 커밋을 직접 실행하지 않고 [assets/template.md](assets/template.md) 형식으로 메시지만 출력한다.

## 추가 참고

- 타입 목록 및 규칙 → [references/api.md](references/api.md)
- 출력 템플릿 → [assets/template.md](assets/template.md)
- 작성 예시 → [examples/](examples/)
