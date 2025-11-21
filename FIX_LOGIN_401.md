# 🔧 Fix Lỗi 401 Login

## ⚠️ Vấn Đề

Vẫn gặp lỗi 401 khi login, có thể do:
1. Service chưa được deploy lại với code mới
2. Cần thử endpoint khác
3. Có thể có vấn đề với security config

## 🧪 Giải Pháp 1: Thử Endpoint Cũ

Có 2 endpoint login, thử endpoint cũ (có thể hoạt động tốt hơn):

### Endpoint Cũ (LoginController)
```bash
curl -X POST 'https://ba-datn-j57y.onrender.com/api/auth/nhan_vien_login' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "pass": "admin123"
  }'
```

**Endpoint này:**
- ✅ Không dùng AuthenticationService (dùng trực tiếp NhanVienService)
- ✅ Có thể hoạt động tốt hơn
- ✅ Đã được test và hoạt động trước đây

## 🧪 Giải Pháp 2: Kiểm Tra Service Đã Deploy Code Mới Chưa

### Bước 1: Vào Render Dashboard
1. Đăng nhập: https://dashboard.render.com
2. Vào Web Service của bạn
3. Xem tab **"Logs"**

### Bước 2: Kiểm Tra Build
- Xem logs có message: `"Fix: Skip JWT filter for public endpoints"` không?
- Nếu không thấy → Service chưa deploy code mới

### Bước 3: Deploy Lại
1. Vào tab **"Manual Deploy"**
2. Click **"Deploy latest commit"**
3. Đợi build và deploy xong (5-10 phút)

## 🧪 Giải Pháp 3: Test Trên Frontend

**Có thể login trên FE được** nếu:
1. FE gọi đúng endpoint
2. FE gửi đúng format request
3. Service đang chạy

### Code Test Trên FE (JavaScript/Angular/React)

#### JavaScript/React
```javascript
const login = async () => {
  try {
    const response = await fetch('https://ba-datn-j57y.onrender.com/api/auth/nhan_vien_login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: 'admin',
        pass: 'admin123'
      })
    });
    
    if (response.ok) {
      const data = await response.json();
      console.log('Login thành công!', data);
      // Lưu token
      if (data.token) {
        localStorage.setItem('token', data.token);
      }
    } else {
      const error = await response.json();
      console.error('Login thất bại:', error);
    }
  } catch (error) {
    console.error('Lỗi:', error);
  }
};
```

#### Angular Service
```typescript
login(username: string, password: string): Observable<any> {
  return this.http.post<any>(
    'https://ba-datn-j57y.onrender.com/api/auth/nhan_vien_login',
    {
      username: username,
      pass: password
    }
  ).pipe(
    tap(response => {
      if (response.token) {
        localStorage.setItem('token', response.token);
      }
    })
  );
}
```

## 📋 Tất Cả Endpoints Login

### 1. Endpoint Cũ (Khuyến nghị thử trước)
```
POST /api/auth/nhan_vien_login
POST /api/auth/khach_hang_login
```

### 2. Endpoint Mới
```
POST /api/auth/login/nhan-vien
POST /api/auth/login/khach-hang
```

## 🔍 Debug Steps

### 1. Kiểm Tra Service Đang Chạy
```bash
curl https://ba-datn-j57y.onrender.com/actuator/health
```

**Kết quả mong đợi:**
```json
{
  "status": "UP"
}
```

### 2. Kiểm Tra Logs Trên Render
1. Vào Render Dashboard
2. Vào Web Service → tab **"Logs"**
3. Tìm các dòng:
   - `"Started MycodeApplication"` → Service đã start
   - `"=== Hoàn thành khởi tạo dữ liệu ban đầu ==="` → DataInitializer đã chạy
   - Lỗi gì không?

### 3. Test Endpoint Cũ
```bash
curl -X POST 'https://ba-datn-j57y.onrender.com/api/auth/nhan_vien_login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","pass":"admin123"}'
```

## ✅ Checklist

- [ ] Service đang chạy (health check = UP)
- [ ] Đã thử endpoint cũ `/api/auth/nhan_vien_login`
- [ ] Đã kiểm tra logs trên Render
- [ ] Đã deploy lại code mới (nếu cần)
- [ ] FE gọi đúng endpoint và format

## 🎯 Kết Luận

**Có thể login trên FE được** nếu:
1. ✅ Dùng endpoint cũ: `/api/auth/nhan_vien_login`
2. ✅ Gửi đúng format: `{"username": "...", "pass": "..."}`
3. ✅ Service đang chạy

**Nếu vẫn lỗi 401:**
1. Kiểm tra logs trên Render
2. Đảm bảo service đã deploy code mới
3. Thử endpoint cũ trước

---

**Thử endpoint cũ ngay và cho tôi biết kết quả!**

