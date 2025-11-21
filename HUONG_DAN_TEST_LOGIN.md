# 🧪 Hướng Dẫn Test Login API

## 📍 Các Endpoint Login

### 1. Login Nhân Viên (Mới - Khuyến nghị)
```
POST /api/auth/login/nhan-vien
```

### 2. Login Khách Hàng (Mới - Khuyến nghị)
```
POST /api/auth/login/khach-hang
```

### 3. Login Nhân Viên (Cũ - Vẫn hoạt động)
```
POST /api/auth/nhan_vien_login
```

### 4. Login Khách Hàng (Cũ - Vẫn hoạt động)
```
POST /api/auth/khach_hang_login
```

---

## 🧪 Cách 1: Test Qua Swagger UI (Local/Dev)

### Bước 1: Kiểm Tra Swagger Có Bật Không

**Local (Development):**
- Swagger **ĐÃ BẬT** ✅
- URL: `http://localhost:8080/swagger-ui/index.html`

**Production (Render):**
- Swagger **ĐÃ TẮT** ❌ (vì bảo mật)
- Không thể dùng Swagger trên production

### Bước 2: Truy Cập Swagger UI

1. **Chạy ứng dụng local:**
```bash
cd /Users/phamhaiphuc/Downloads/DATN/BE\ -\ Copy/ba_datn
./mvnw spring-boot:run
```

2. **Mở trình duyệt:**
```
http://localhost:8080/swagger-ui/index.html
```

3. **Tìm endpoint login:**
   - Tìm section **"auth-controller"**
   - Click vào `POST /api/auth/login/nhan-vien`
   - Click **"Try it out"**

4. **Nhập thông tin:**
```json
{
  "username": "admin",
  "pass": "admin123"
}
```

5. **Click "Execute"**

6. **Xem kết quả:**
   - Response sẽ hiển thị token và thông tin user

---

## 🧪 Cách 2: Test Qua cURL (Terminal)

### Test Login Nhân Viên (Admin)

```bash
curl -X POST https://ba-datn-backend.onrender.com/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "pass": "admin123"
  }'
```

**Hoặc local:**
```bash
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "pass": "admin123"
  }'
```

### Test Login Khách Hàng

```bash
curl -X POST https://ba-datn-backend.onrender.com/api/auth/login/khach-hang \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer_username",
    "pass": "customer_password"
  }'
```

### Lưu Token Để Dùng Sau

```bash
# Lưu response vào file
curl -X POST https://ba-datn-backend.onrender.com/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "pass": "admin123"
  }' > login_response.json

# Xem token
cat login_response.json | grep -o '"token":"[^"]*"' | cut -d'"' -f4
```

---

## 🧪 Cách 3: Test Qua Postman

### Bước 1: Tạo Request Mới

1. Mở Postman
2. Click **"New"** → **"HTTP Request"**
3. Chọn method: **POST**

### Bước 2: Nhập URL

**Production:**
```
https://ba-datn-backend.onrender.com/api/auth/login/nhan-vien
```

**Local:**
```
http://localhost:8080/api/auth/login/nhan-vien
```

### Bước 3: Cấu Hình Headers

Vào tab **"Headers"** và thêm:
```
Key: Content-Type
Value: application/json
```

### Bước 4: Nhập Body

Vào tab **"Body"**:
1. Chọn **"raw"**
2. Chọn **"JSON"** từ dropdown
3. Nhập:
```json
{
  "username": "admin",
  "pass": "admin123"
}
```

### Bước 5: Send Request

Click **"Send"** và xem response.

### Bước 6: Lưu Token

1. Copy token từ response
2. Tạo request mới cho API cần authentication
3. Vào tab **"Authorization"**
4. Chọn **"Bearer Token"**
5. Paste token vào

---

## 🧪 Cách 4: Test Qua Browser Console (JavaScript)

### Mở Browser Console

1. Mở trình duyệt (Chrome/Firefox)
2. Nhấn `F12` hoặc `Ctrl+Shift+I` (Windows) / `Cmd+Option+I` (Mac)
3. Vào tab **"Console"**

### Test Login

```javascript
// Test login nhân viên
fetch('https://ba-datn-backend.onrender.com/api/auth/login/nhan-vien', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'admin',
    pass: 'admin123'
  })
})
.then(response => response.json())
.then(data => {
  console.log('Login thành công!');
  console.log('Token:', data.token);
  console.log('User:', data);
  
  // Lưu token vào localStorage
  if (data.token) {
    localStorage.setItem('token', data.token);
    console.log('Token đã được lưu vào localStorage');
  }
})
.catch(error => {
  console.error('Lỗi:', error);
});
```

### Dùng Token Để Gọi API Khác

```javascript
// Lấy token từ localStorage
const token = localStorage.getItem('token');

// Gọi API với token
fetch('https://ba-datn-backend.onrender.com/api/auth/me', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  }
})
.then(response => response.json())
.then(data => {
  console.log('User info:', data);
})
.catch(error => {
  console.error('Lỗi:', error);
});
```

---

## 📋 Request Body Format

Tất cả các endpoint login đều dùng format này:

```json
{
  "username": "tai_khoan",
  "pass": "mat_khau"
}
```

**Lưu ý:**
- Field là `pass` (không phải `password`)
- Field là `username` (không phải `email` hoặc `taiKhoan`)

---

## 📋 Response Format

### Thành Công (200 OK)

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIs...",
  "user": {
    "id": 1,
    "ma": "NV001",
    "hoTen": "Administrator",
    "email": "admin@datn.com",
    "taiKhoan": "admin",
    "chucVu": {
      "id": 1,
      "ten": "ADMIN"
    },
    "trangThai": 1
  }
}
```

### Lỗi (401 Unauthorized)

```json
{
  "message": "Tên đăng nhập hoặc mật khẩu không đúng",
  "status": 401
}
```

---

## ✅ Tài Khoản Test

### Admin (Nhân Viên)
```
Username: admin
Password: admin123
```

**Lưu ý:** Tài khoản này được tạo tự động bởi `DataInitializer` khi deploy lên Render.

---

## 🔍 Kiểm Tra Service Đang Chạy

### Test Health Check

```bash
# Production
curl https://ba-datn-backend.onrender.com/actuator/health

# Local
curl http://localhost:8080/actuator/health
```

**Kết quả mong đợi:**
```json
{
  "status": "UP"
}
```

---

## 🐛 Troubleshooting

### Lỗi: "Connection refused"
**Nguyên nhân:** Service chưa chạy hoặc đang sleep (Free Tier)

**Giải pháp:**
1. Kiểm tra service đang chạy trên Render Dashboard
2. Nếu sleep, đợi 30-60 giây để wake up
3. Hoặc ping health check trước

### Lỗi: "401 Unauthorized"
**Nguyên nhân:** Username hoặc password sai

**Giải pháp:**
1. Kiểm tra username và password
2. Đảm bảo tài khoản đã được tạo (check database)
3. Kiểm tra tài khoản có `trangThai = 1` (active)

### Lỗi: "404 Not Found"
**Nguyên nhân:** URL sai hoặc endpoint không tồn tại

**Giải pháp:**
1. Kiểm tra URL có đúng không
2. Kiểm tra service đã deploy thành công chưa
3. Xem logs trên Render Dashboard

### Lỗi: "500 Internal Server Error"
**Nguyên nhân:** Lỗi server

**Giải pháp:**
1. Xem logs trên Render Dashboard
2. Kiểm tra database connection
3. Kiểm tra environment variables

---

## 📝 Checklist Test Login

- [ ] Service đang chạy (health check = UP)
- [ ] Đã có tài khoản admin trong database
- [ ] Request body đúng format (username, pass)
- [ ] Content-Type header = application/json
- [ ] Nhận được token trong response
- [ ] Token có thể dùng để gọi API khác

---

## 🎯 Next Steps

Sau khi login thành công:

1. **Lưu token** vào localStorage hoặc state management
2. **Gửi token** trong mọi request cần authentication:
   ```
   Authorization: Bearer <token>
   ```
3. **Xử lý token hết hạn** (401) → redirect về login
4. **Refresh token** nếu có (endpoint: `/api/auth/refresh-token`)

---

**Chúc bạn test thành công! 🚀**

