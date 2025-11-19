# 🚀 Hướng Dẫn Push Code Lên GitHub

## ✅ Đã Sẵn Sàng:
- ✅ Remote đã được cấu hình: `https://github.com/hzxphuc03/ba_datn.git`
- ✅ Code đã được commit
- ✅ Branch đã là `main`

## 📤 Cách Push Code:

### Cách 1: Push Trực Tiếp (Sẽ Yêu Cầu Đăng Nhập)

Mở Terminal và chạy:

```bash
cd "/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn"
git push -u origin main
```

**Khi được hỏi:**
- **Username**: `hzxphuc03`
- **Password**: **Personal Access Token** (KHÔNG phải mật khẩu GitHub)

---

### Cách 2: Tạo Personal Access Token (Nếu Chưa Có)

1. **Vào trang tạo token:**
   - Truy cập: https://github.com/settings/tokens
   - Hoặc: GitHub → Profile → Settings → Developer settings → Personal access tokens → Tokens (classic)

2. **Tạo token mới:**
   - Click **"Generate new token"** → **"Generate new token (classic)"**
   - **Note**: Đặt tên dễ nhớ (ví dụ: `ba_datn_push`)
   - **Expiration**: Chọn thời hạn (ví dụ: 90 days hoặc No expiration)
   - **Select scopes**: ✅ Tích vào **`repo`** (full control of private repositories)
   - Click **"Generate token"** ở cuối trang

3. **Copy token:**
   - ⚠️ **QUAN TRỌNG**: Token chỉ hiển thị 1 lần, copy ngay!
   - Token sẽ có dạng: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

4. **Dùng token:**
   - Khi push, dùng token này làm **password**
   - Username vẫn là: `hzxphuc03`

---

### Cách 3: Push Với Token Trong URL (Nhanh Nhất) ⭐

Sau khi có token, chạy:

```bash
cd "/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn"

# Thay YOUR_TOKEN bằng token bạn vừa tạo
git remote set-url origin https://YOUR_TOKEN@github.com/hzxphuc03/ba_datn.git

# Push code
git push -u origin main
```

**Ví dụ:**
```bash
git remote set-url origin https://ghp_abc123xyz@github.com/hzxphuc03/ba_datn.git
git push -u origin main
```

> ⚠️ **Lưu ý**: Token sẽ được lưu trong URL. Nếu lo ngại bảo mật, dùng Cách 1 hoặc 2.

---

### Cách 4: Dùng Script Tự Động

```bash
cd "/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn"
./push-to-github.sh
```

---

## ✅ Sau Khi Push Thành Công:

1. **Kiểm tra trên GitHub:**
   - Vào: https://github.com/hzxphuc03/ba_datn
   - Bạn sẽ thấy tất cả code đã được upload

2. **Refresh trang GitHub** nếu chưa thấy code

3. **Kiểm tra các file:**
   - ✅ README.md
   - ✅ src/ (tất cả code Java)
   - ✅ pom.xml
   - ✅ .gitignore
   - ✅ application.properties.example

---

## 🔒 Lưu Ý Bảo Mật:

- ✅ File `application.properties` (chứa mật khẩu) **KHÔNG** được commit
- ✅ File `application-prod.properties` cũng được bảo vệ
- ✅ Chỉ có `application.properties.example` được commit

---

## ❌ Xử Lý Lỗi:

### Lỗi: "Authentication failed"
- Kiểm tra username đúng chưa: `hzxphuc03`
- Đảm bảo dùng **Personal Access Token**, không phải mật khẩu GitHub
- Token phải có scope `repo`

### Lỗi: "Permission denied"
- Kiểm tra token còn hiệu lực chưa
- Tạo token mới nếu cần

### Lỗi: "Repository not found"
- Kiểm tra repository đã được tạo trên GitHub chưa
- Kiểm tra URL remote: `git remote -v`

---

**Chạy lệnh push và nhập thông tin khi được hỏi! 🚀**

