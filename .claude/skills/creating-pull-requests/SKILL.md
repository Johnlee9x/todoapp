---
name: creating-pull-requests
description: Use this skill BEFORE drafting or opening any pull request for todoapp — trigger the moment you decide a PR will be created or updated, not at the moment you run `gh pr create`. Covers deciding what goes in the title/body, sizing the description to the diff, and the exact `gh` commands to run. Also use when the user asks to "update PR", "sửa lại nội dung PR", "mở PR", "tạo pull request", or when a branch is ready to be turned into a PR.
---

# Tạo & cập nhật Pull Request cho todoapp

Repo này là dự án cá nhân (`Johnlee9x/todoapp`), không phải team lớn — mô tả PR
tồn tại để **chính bạn** (hoặc bạn tương lai) hiểu lại quyết định đã đưa ra, không
phải để thuyết phục một reviewer lạ. Ngắn, đúng, có lý do — hơn là đầy đủ mọi mục.

## Luật cứng — không có ngoại lệ

- **Luôn viết title + body bằng tiếng Việt.** Đây là quy ước đã có từ PR #1, #2.
- **Luôn tạo PR ở chế độ draft** (`gh pr create --draft ...`). Người dùng tự bấm
  "Ready for review" khi họ muốn.
- **Không có bất kỳ dấu vết AI/Claude nào trong PR**: không `Co-Authored-By:
  Claude`, không dòng "🤖 Generated with Claude Code", không câu nào nhắc tới
  "Claude"/"AI"/"agent" trong title hoặc body. (Khác với commit message thông
  thường — luật này chỉ áp dụng cho *nội dung PR*.)
- **Không bịa lý do.** Nếu không suy ra được "vì sao" từ diff/commit
  message/tên nhánh/hội thoại, dùng `AskUserQuestion` hỏi thẳng: "Vì sao thay
  đổi này tồn tại?" / "Trước đây nó bị lỗi/thiếu gì?". Một mô tả thiếu phần
  "vì sao" còn tốt hơn một mô tả bịa lý do.

## Quy trình

### 1. Xác định tạo mới hay cập nhật

```bash
gh pr view --json number,title,body,baseRefName,url 2>/dev/null
```

### 2. Lấy context thật, không đoán

```bash
BASE=$(gh pr view --json baseRefName -q '.baseRefName' 2>/dev/null || echo "main")
git diff $BASE...HEAD --stat   # để phân loại size
git diff $BASE...HEAD          # đọc thật, không chỉ nhìn stat
git log $BASE..HEAD --oneline
```

### 3. Phân loại theo size trước khi viết

Diff càng nhỏ, mô tả càng ngắn — mô tả dài hơn diff là dấu hiệu "viết cho có".

- **Nhỏ** (một concern, diff nhỏ): chỉ `## Tóm tắt` — 1 đoạn ngắn nêu vấn đề +
  cách giải quyết. Không cần Test plan riêng nếu build/test không đổi.
- **Vừa/Lớn** (nhiều file, nhiều concern, hoặc thay đổi kiến trúc): dùng đủ
  khuôn ở mục "Khuôn mẫu" bên dưới.

### 4. Viết, rồi tạo/cập nhật qua file tạm

Không truyền body inline qua `--body` hay heredoc — luôn ghi ra file rồi dùng
`--body-file`, để tránh lỗi escape ký tự và dễ soát lại trước khi gửi.

```bash
# Tạo mới (luôn --draft)
gh pr create --draft --base main --title "<tiêu đề tiếng Việt>" \
  --body-file /tmp/pr-body.md

# Cập nhật PR đang mở
gh pr edit <number> --title "..." --body-file /tmp/pr-body.md
```

Dùng thư mục scratchpad của session cho file tạm, không phải `/tmp` trực tiếp
nếu có scratchpad được cấp.

## Khuôn mẫu (PR vừa/lớn)

```markdown
## Tóm tắt

[Vấn đề là gì / vì sao đổi — 1-3 câu, có số liệu/lỗi cụ thể nếu có.
Sau đó: đã làm gì để giải quyết.]

- [Bullet theo từng phần việc chính, không liệt kê từng file — diff đã show
  file rồi]

## Test plan

- [x] `./gradlew assembleDebug` — BUILD SUCCESSFUL
- [x] `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL
- [ ] (lệnh nào chưa chạy thật thì để trống, đừng tick khống)
```

Bỏ mục nào không cần — VD PR chỉ đổi `.gitignore`/tài liệu thì không cần Test
plan dạng Gradle, chỉ cần ghi "Không có thay đổi code/build, không cần build
lại" như PR #2 đã làm.

## Tránh văn phong lộ AI

- Không mở câu bằng "PR này thêm/sửa/triển khai...". Đi thẳng vào vấn đề: nêu
  hiện trạng lỗi/thiếu trước, rồi mới tới cách giải quyết (xem cách PR #1 mở
  đầu: "`settings.gradle.kts` đã khai báo sẵn... nhưng chúng chưa từng được
  tạo trên đĩa").
- Không dùng cụm rỗng không có lý do cụ thể đi kèm: "cải thiện chất lượng
  code", "dọn dẹp technical debt", "refactor cho gọn hơn" — nếu không giải
  thích được cụ thể cái gì sai/khó chịu trước đó, đừng viết lý do đó ra.
- Không liệt kê lại từng file đã đổi kiểu tường thuật ("đã sửa file A, sau đó
  sửa file B") — diff đã thể hiện việc đó, mô tả chỉ cần nói *vì sao*.

## Đặt tên nhánh

Theo đúng quy ước đã dùng trong repo: `<loại>/<mô-tả-ngắn-kebab-case>`.

Loại đã dùng: `refactor/` (đổi kiến trúc), `chore/` (dọn dẹp không đổi hành vi),
`docs/` (chỉ tài liệu). Thêm `feat/`, `fix/` khi phù hợp.

Ví dụ có sẵn trong repo: `refactor/modularize-gradle-structure`,
`chore/ignore-idea-local-files`, `docs/enrich-claude-md`.

Mỗi PR giải quyết đúng một việc — nếu phát hiện việc không liên quan trong lúc
làm (VD dọn `.idea/` trong khi đang tách module), tách nhánh/PR riêng, đừng
trộn vào PR đang làm (xem cách PR #2 tách khỏi PR #1).
