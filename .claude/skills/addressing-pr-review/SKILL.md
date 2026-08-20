---
name: addressing-pr-review
description: Use this skill when the user asks to check, address, respond to, or resolve reviewer comments on a pull request — e.g. "check comment trên PR", "fix theo góp ý review", "trả lời comment tech lead", "xử lý feedback PR", "resolve PR feedback". Fetches unresolved GitHub review threads and top-level PR comments, categorizes each, fixes code with a build/test verification gate, pushes before claiming anything is fixed, and always confirms with the user before posting anything publicly (reply, resolve, or re-review request).
---

# Xử lý comment review trên PR

Khác với skill [[creating-pull-requests]] (tạo PR mới), skill này xử lý **feedback
đã có** trên 1 PR đang mở — của tech lead hoặc reviewer thật, không phải bot
Code Review của Anthropic (cơ chế của bot đó khác hẳn: tự resolve khi bạn push
fix, không cần skill này can thiệp).

## Nguyên tắc cốt lõi — an toàn trước, tự động sau

- **Không post bất kỳ thứ gì công khai (reply, resolve, comment) mà chưa cho
  bạn xem trước và xác nhận qua `AskUserQuestion`.** Áp dụng cho MỌI thread,
  không chỉ case agent nghi ngờ/không đồng ý — reply/resolve là hành động
  hiển thị cho tech lead thấy, không phải thao tác nội bộ.
- **Không bao giờ reply "đã sửa" hay resolve thread trước khi code đó thật
  sự đã push lên nhánh PR và build/test pass.** Không được post "Fixed" chỉ
  vì đã sửa xong trên máy — tech lead đọc reply "Fixed" phải thấy đúng code
  đó trên PR ngay lúc đó, không phải một lúc sau.
- **Không nhắc "Claude"/"AI"/"agent" trong reply, comment, hay commit** —
  khớp luật đã chốt ở skill `creating-pull-requests`.
- **Không tự quyết "won't fix"** khi agent nghĩ góp ý sai — luôn hỏi lại
  người dùng trước khi reply theo hướng đó.
- **Không giả định có bot review nào khác trong repo** (không có `@codex`
  hay bot tương tự) — không tự động yêu cầu re-review từ bên thứ ba nào.

## Yêu cầu trước

- `gh` CLI đã authenticated (`gh auth status`).
- Đang trong git repo có remote GitHub, biết số PR cần xử lý.

## Quy trình

### 1. Xác định PR

```bash
gh pr view --json number,url,headRefName
gh repo view --json owner,name
```

Nếu không chạy trên đúng nhánh của PR, hỏi lại số PR trước khi tiếp tục. Lấy
`OWNER`, `REPO`, `PR_NUMBER` từ đây để dùng cho các lệnh bên dưới.

### 2. Lấy toàn bộ feedback chưa xử lý — cả 2 loại

**a. Comment gắn vào dòng code cụ thể** (review thread, có thể resolve):

```bash
gh api graphql -f query='
query {
  repository(owner: "OWNER", name: "REPO") {
    pullRequest(number: PR_NUMBER) {
      id
      reviewThreads(first: 100) {
        nodes {
          id
          isResolved
          path
          line
          comments(first: 10) {
            nodes { id databaseId body author { login } }
          }
        }
      }
    }
  }
}'
```

Lọc `isResolved: false`.

**b. Comment chung trên PR, không gắn dòng nào** (top-level, GitHub không có
khái niệm "resolved" cho loại này — chỉ reply được, không resolve được):

```bash
gh api repos/OWNER/REPO/issues/PR_NUMBER/comments
```

Nếu cả 2 danh sách đều rỗng — báo "Không có comment nào cần xử lý" và dừng.

### 3. Phân loại từng mục, hiển thị tóm tắt trước khi làm gì tiếp

Với mỗi thread/comment: đọc file/dòng liên quan nếu có (`Read`), hiểu ý tech
lead, xếp vào 1 trong 4 loại:

- **Cần sửa code**
- **Cần sửa doc/comment**
- **Chỉ là câu hỏi** — trả lời bằng lời, không sửa code
- **Nghi ngờ/không đồng ý** — không tự quyết, để hỏi lại ở bước 5

Hiển thị bảng: nguồn (review thread / comment chung), file:dòng (nếu có),
loại, tóm tắt góp ý — cho người dùng thấy toàn cảnh trước khi sửa bất cứ gì.

### 4. Sửa từng mục cần sửa code/doc — chưa commit vội

a. Đọc file, sửa bằng `Edit`.

b. **Verify bắt buộc** — tra `CLAUDE.md` để chạy đúng lệnh Gradle của module
   vừa sửa, ví dụ `./gradlew :feature:task:testDebugUnitTest`. Nếu không chắc
   phạm vi ảnh hưởng, tối thiểu chạy `./gradlew assembleDebug`. **Lệnh fail
   thì dừng lại, báo lỗi cho người dùng — không đi tiếp.**

c. Soạn nội dung reply dự kiến: tiếng Việt, ngắn gọn, nêu đã sửa gì và vì
   sao — không liệt kê lại toàn bộ diff, không nhắc AI.

Với mục "chỉ là câu hỏi": bỏ qua 4a/4b, chỉ soạn câu trả lời.

### 5. Xác nhận trước khi commit/post — bắt buộc, không có ngoại lệ

Dùng `AskUserQuestion` hiển thị đầy đủ cho từng mục:

- Nguồn + file:dòng (nếu có), tác giả comment gốc
- Fix đã áp dụng là gì (hoặc "không sửa code, chỉ trả lời")
- Kết quả verify (lệnh nào, pass/fail)
- Nội dung reply dự kiến
- Có resolve sau khi reply không (chỉ hỏi mục này với review thread, comment
  chung không resolve được)

Với mục "nghi ngờ/không đồng ý": câu hỏi thay bằng "Bạn đồng ý với góp ý này
không, hay muốn mình reply giải thích vì sao giữ nguyên?" — chờ người dùng
quyết, không tự suy diễn.

### 6. Commit + push TẤT CẢ fix đã được duyệt ở bước 5

Gộp vào 1 (hoặc vài) commit rõ ràng, tiếng Việt, **không** có
`Co-Authored-By`/nhắc AI:

```
fix: xử lý góp ý review PR #<n>

- <mô tả fix 1, tham chiếu comment/tác giả nào>
- <mô tả fix 2>
```

Push lên đúng nhánh hiện có của PR (không tạo nhánh mới) — push tự cập nhật
PR. **Chỉ sang bước 7 sau khi push thành công.**

### 7. Post reply + resolve — chỉ sau khi code đã lên PR thật

```bash
# Reply vào review thread — ghi nội dung ra file trước để tránh lỗi escape
gh api --method POST repos/OWNER/REPO/pulls/PR_NUMBER/comments/COMMENT_DATABASE_ID/replies \
  -f body=@/path/to/reply-body.txt

# Resolve — chỉ với review thread (không áp dụng cho comment chung)
gh api graphql -f query='
mutation {
  resolveReviewThread(input: {threadId: "THREAD_NODE_ID"}) {
    thread { isResolved }
  }
}'

# Reply comment chung (top-level, không có gì để resolve)
gh pr comment PR_NUMBER --body-file /path/to/reply-body.txt
```

### 8. Tổng kết

Báo cho người dùng: số mục đã xử lý / đã resolve / còn để ngỏ (case chưa
chốt ở bước 5), file đã sửa, commit đã push, mục nào cố tình chưa trả lời và
vì sao.

## Không làm

- Không chạy một mạch từ bước 1 đến 6 mà không dừng ở bước 5 cho từng mục.
- Không reply "đã sửa" hoặc resolve trước khi push thành công (bước 6).
- Không resolve comment chung không gắn dòng — loại đó không có trạng thái
  "resolved" trong GitHub, chỉ reply được.
- Không tự động yêu cầu re-review từ bot nào không tồn tại trong repo.
- Không nhắc "Claude"/"AI"/"agent" trong bất kỳ reply, comment, hay commit
  nào skill này tạo ra.
