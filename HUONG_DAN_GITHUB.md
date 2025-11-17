# 🚀 Hướng Dẫn Đẩy Dự Án Lên GitHub

## Bước 1: Tạo Repository Mới trên GitHub

### 1.1. Đăng nhập GitHub
- Truy cập: https://github.com
- Đăng nhập vào tài khoản của bạn

### 1.2. Tạo Repository Mới
1. Click vào biểu tượng **"+"** ở góc trên bên phải
2. Chọn **"New repository"**

3. Điền thông tin:
   - **Repository name**: `ba_datn` (hoặc tên bạn muốn)
   - **Description**: `DATN Backend - Hệ thống quản lý bán hàng`
   - **Visibility**: 
     - ✅ **Private** (khuyến nghị - chỉ bạn mới thấy)
     - ⚪ Public (ai cũng có thể xem)
   - ⚠️ **QUAN TRỌNG**: 
     - ❌ **KHÔNG** tích vào "Add a README file"
     - ❌ **KHÔNG** tích vào "Add .gitignore"
     - ❌ **KHÔNG** tích vào "Choose a license"

4. Click nút **"Create repository"**

### 1.3. Copy Repository URL
Sau khi tạo xong, GitHub sẽ hiển thị URL repository. Copy URL này:
- **HTTPS**: `https://github.com/your-username/ba_datn.git`
- **SSH**: `git@github.com:your-username/ba_datn.git`

> 💡 **Lưu ý**: 
> - Nếu đã cấu hình SSH key → dùng SSH URL
> - Nếu chưa → dùng HTTPS URL

---

## Bước 2: Cấu Hình Git (Nếu Chưa Có)

### Kiểm tra Git đã cấu hình chưa:
```bash
git config --global user.name
git config --global user.email
```

### Nếu chưa có, cấu hình:
```bash
git config --global user.name "Tên của bạn"
git config --global user.email "email@example.com"
```

---

## Bước 3: Đẩy Code Lên GitHub

### Cách 1: Dùng Script Tự Động (Khuyến Nghị) ⭐

```bash
# Di chuyển vào thư mục dự án
cd "/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn"

# Chạy script (thay YOUR_USERNAME và REPO_NAME)
./setup-new-repo.sh git@github.com:YOUR_USERNAME/REPO_NAME.git

# Hoặc dùng HTTPS:
./setup-new-repo.sh https://github.com/YOUR_USERNAME/REPO_NAME.git
```

**Ví dụ:**
```bash
./setup-new-repo.sh git@github.com:phamhaiphuc/ba_datn.git
```

---

### Cách 2: Làm Thủ Công

#### 2.1. Kiểm tra trạng thái hiện tại
```bash
cd "/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn"
git status
```

#### 2.2. Xóa remote cũ (nếu có)
```bash
git remote remove origin
```

#### 2.3. Thêm remote mới
```bash
# Dùng SSH (nếu đã cấu hình SSH key)
git remote add origin git@github.com:YOUR_USERNAME/ba_datn.git

# HOẶC dùng HTTPS
git remote add origin https://github.com/YOUR_USERNAME/ba_datn.git
```

**Kiểm tra remote:**
```bash
git remote -v
```

#### 2.4. Commit các thay đổi (nếu có)
```bash
# Xem các file thay đổi
git status

# Thêm tất cả file vào staging
git add .

# Commit
git commit -m "Initial commit - DATN Backend Project"
```

#### 2.5. Đổi tên branch thành main (nếu cần)
```bash
# Kiểm tra branch hiện tại
git branch

# Đổi tên branch thành main
git branch -M main
```

#### 2.6. Push code lên GitHub
```bash
# Push lên branch main
git push -u origin main
```

> ⚠️ **Nếu dùng HTTPS lần đầu**, GitHub sẽ yêu cầu đăng nhập:
> - Username: tên GitHub của bạn
> - Password: **Personal Access Token** (không phải mật khẩu GitHub)
>   - Tạo token tại: https://github.com/settings/tokens
>   - Chọn scope: `repo` (full control)

---

## Bước 4: Xác Nhận

1. Vào repository trên GitHub: `https://github.com/YOUR_USERNAME/ba_datn`
2. Kiểm tra xem code đã được push lên chưa
3. Xem các file đã được upload

---

## Xử Lý Lỗi Thường Gặp

### ❌ Lỗi: "Permission denied (publickey)"
**Nguyên nhân**: Chưa cấu hình SSH key

**Giải pháp 1: Dùng HTTPS thay vì SSH**
```bash
git remote set-url origin https://github.com/YOUR_USERNAME/ba_datn.git
git push -u origin main
```

**Giải pháp 2: Cấu hình SSH key**
1. Tạo SSH key:
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```
2. Copy public key:
```bash
cat ~/.ssh/id_ed25519.pub
```
3. Thêm vào GitHub:
   - Vào: https://github.com/settings/keys
   - Click "New SSH key"
   - Paste key và lưu

---

### ❌ Lỗi: "Updates were rejected"
**Nguyên nhân**: Repository trên GitHub đã có file

**Giải pháp:**
```bash
# Pull code từ GitHub trước
git pull origin main --allow-unrelated-histories

# Giải quyết conflict (nếu có), sau đó:
git push -u origin main
```

---

### ❌ Lỗi: "Authentication failed"
**Nguyên nhân**: Sai thông tin đăng nhập

**Giải pháp:**
1. Tạo Personal Access Token:
   - Vào: https://github.com/settings/tokens
   - Click "Generate new token (classic)"
   - Chọn scope: `repo`
   - Copy token
2. Dùng token làm password khi push

---

### ❌ Lỗi: "fatal: not a git repository"
**Nguyên nhân**: Chưa khởi tạo git

**Giải pháp:**
```bash
git init
```

---

## Sau Khi Push Thành Công

### 1. Cập nhật README.md
Tạo file README.md mô tả dự án:
```bash
# Mở file README.md và cập nhật nội dung
```

### 2. Thêm .gitignore
File `.gitignore` đã được cập nhật để bảo vệ:
- ✅ `application.properties` (chứa mật khẩu)
- ✅ `application-prod.properties`
- ✅ File Excel
- ✅ File build (target/)

### 3. Tạo file cấu hình mẫu
File `application.properties.example` đã được tạo để người khác biết cách cấu hình.

---

## Lệnh Git Hữu Ích

```bash
# Xem trạng thái
git status

# Xem lịch sử commit
git log --oneline

# Xem các branch
git branch -a

# Xem remote
git remote -v

# Thêm file cụ thể
git add filename.java

# Commit với message
git commit -m "Mô tả thay đổi"

# Push lên GitHub
git push origin main

# Pull code từ GitHub
git pull origin main
```

---

## Checklist Hoàn Thành

- [ ] Đã tạo repository trên GitHub
- [ ] Đã xóa remote cũ
- [ ] Đã thêm remote mới
- [ ] Đã commit các thay đổi
- [ ] Đã push code lên GitHub
- [ ] Đã kiểm tra code trên GitHub
- [ ] Đã cập nhật README.md (tùy chọn)
- [ ] Đã kiểm tra .gitignore bảo vệ file nhạy cảm

---

## Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
- 📖 [GitHub Docs](https://docs.github.com)
- 🔑 [SSH Key Setup](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
- 🔐 [Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

---

**Chúc bạn thành công! 🎉**

