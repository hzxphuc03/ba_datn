# Hướng Dẫn Đẩy Dự Án Lên Git Repository Mới

## Bước 1: Tạo Repository Mới trên GitHub/GitLab

### Nếu dùng GitHub:
1. Đăng nhập vào GitHub
2. Click nút **"New"** hoặc **"+"** → **"New repository"**
3. Đặt tên repository (ví dụ: `ba_datn` hoặc `datn-backend`)
4. Chọn **Private** hoặc **Public**
5. **KHÔNG** tích vào "Initialize with README"
6. Click **"Create repository"**

### Nếu dùng GitLab:
1. Đăng nhập vào GitLab
2. Click **"New project"** → **"Create blank project"**
3. Đặt tên project
4. Chọn visibility level
5. **KHÔNG** tích vào "Initialize repository with a README"
6. Click **"Create project"**

## Bước 2: Thay Đổi Remote URL

Sau khi tạo repository mới, bạn sẽ có URL như:
- GitHub: `https://github.com/your-username/ba_datn.git` hoặc `git@github.com:your-username/ba_datn.git`
- GitLab: `https://gitlab.com/your-username/ba_datn.git` hoặc `git@gitlab.com:your-username/ba_datn.git`

### Thay đổi remote URL:

```bash
# Xóa remote cũ
git remote remove origin

# Thêm remote mới (thay YOUR_URL bằng URL repository mới của bạn)
git remote add origin YOUR_URL

# Kiểm tra lại
git remote -v
```

## Bước 3: Commit Tất Cả Thay Đổi

```bash
# Xem trạng thái
git status

# Thêm tất cả file (trừ những file trong .gitignore)
git add .

# Commit
git commit -m "Initial commit - DATN Backend Project"

# Hoặc nếu muốn commit từng phần:
git add .
git commit -m "Update project files"
```

## Bước 4: Push Lên Repository Mới

```bash
# Push lên branch main (hoặc master)
git push -u origin main

# Nếu branch hiện tại không phải main, đổi tên branch:
git branch -M main
git push -u origin main

# Hoặc nếu repository dùng branch master:
git push -u origin master
```

## Lưu Ý Quan Trọng:

1. **File `application.properties` đã được thêm vào `.gitignore`** để bảo vệ thông tin nhạy cảm
2. Đã tạo file `application.properties.example` làm template
3. Khi clone repository mới, cần copy `application.properties.example` thành `application.properties` và điền thông tin thực tế

## Nếu Gặp Lỗi:

### Lỗi: "Updates were rejected"
```bash
# Nếu repository mới đã có file, cần pull trước:
git pull origin main --allow-unrelated-histories
git push -u origin main
```

### Lỗi: "Permission denied"
- Kiểm tra SSH key đã được thêm vào GitHub/GitLab chưa
- Hoặc dùng HTTPS với Personal Access Token

## Sau Khi Push Thành Công:

1. Vào repository trên GitHub/GitLab để kiểm tra
2. Cập nhật README.md với thông tin dự án
3. Thêm file `application.properties.example` vào repository để người khác biết cách cấu hình

