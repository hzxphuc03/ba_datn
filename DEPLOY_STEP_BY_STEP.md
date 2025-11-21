# 🚀 Hướng Dẫn Deploy Từng Bước Lên Render

## 📋 Bước 1: Tạo JWT Secret Key

Trước khi deploy, bạn cần tạo JWT secret key:

```bash
openssl rand -base64 32
```

**Lưu lại secret key này** - bạn sẽ cần nó ở bước sau.

**Ví dụ output:**
```
GP3PLcUTTgmdYi5jVt7p6PQ+Ttd5wStcfrRFFswGf1U=
```

---

## 📋 Bước 2: Đăng Nhập Render Dashboard

1. Mở trình duyệt và vào: https://dashboard.render.com
2. Đăng nhập bằng GitHub account (nếu chưa có tài khoản, đăng ký miễn phí)

---

## 📋 Bước 3: Tạo Web Service

### 3.1. Click "New +"
- Ở góc trên bên phải, click nút **"New +"**
- Chọn **"Web Service"**

### 3.2. Connect GitHub Repository
1. Render sẽ hỏi bạn connect GitHub repository
2. Click **"Connect account"** nếu chưa connect
3. Chọn repository: **`hzxphuc03/ba_datn`**
4. Click **"Connect"**

### 3.3. Cấu Hình Cơ Bản
- **Name**: `ba-datn-backend` (hoặc tên bạn muốn)
- **Region**: Chọn region gần bạn nhất (ví dụ: `Oregon (US West)`)
- **Branch**: `main`
- **Root Directory**: Để trống (hoặc `/`)

---

## 📋 Bước 4: Cấu Hình Runtime (QUAN TRỌNG!)

### 4.1. Chọn Runtime
- **Runtime**: Chọn **`Docker`** ⚠️ (QUAN TRỌNG - không chọn Node hoặc Java)

### 4.2. Build & Start Commands
- **Build Command**: Để **TRỐNG** (Render sẽ tự động build từ Dockerfile)
- **Start Command**: Để **TRỐNG** (Render sẽ tự động chạy từ Dockerfile)

### 4.3. Plan
- **Plan**: Chọn **`Free`** (hoặc paid plan nếu bạn có)

---

## 📋 Bước 5: Cấu Hình Environment Variables

Click vào tab **"Environment"** và thêm các biến sau:

### 5.1. Database (Bắt buộc)

Click **"Add Environment Variable"** và thêm từng biến:

```
Key: SPRING_DATASOURCE_URL
Value: jdbc:postgresql://dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db
```

```
Key: SPRING_DATASOURCE_USERNAME
Value: ba_datn_db_user
```

```
Key: SPRING_DATASOURCE_PASSWORD
Value: lNxER7uBbNYw60imnHx6coVCKyiUvSDH
```

```
Key: SPRING_DATASOURCE_DRIVER
Value: org.postgresql.Driver
```

```
Key: HIBERNATE_DIALECT
Value: org.hibernate.dialect.PostgreSQLDialect
```

### 5.2. Spring Profile

```
Key: SPRING_PROFILES_ACTIVE
Value: prod
```

### 5.3. Port

```
Key: PORT
Value: 8080
```

### 5.4. JWT (Bắt buộc)

Dùng JWT secret key đã tạo ở Bước 1:

```
Key: JWT_SECRET
Value: <paste-secret-key-đã-tạo-ở-bước-1>
```

**Ví dụ:**
```
Key: JWT_SECRET
Value: aB3dE5fG7hI9jK1lM3nO5pQ7rS9tU1vW3xY5zA7bC9dE1fG3hI5jK7lM9nO1pQ3
```

```
Key: JWT_EXPIRATION
Value: 86400000
```

```
Key: JWT_REFRESH_EXPIRATION
Value: 604800000
```

### 5.5. Email (Tùy chọn - nếu có chức năng gửi email)

Nếu bạn cần gửi email (OTP, thông báo, v.v.):

1. **Tạo Gmail App Password:**
   - Vào: https://myaccount.google.com/security
   - Bật **2-Step Verification** (nếu chưa bật)
   - Vào: https://myaccount.google.com/apppasswords
   - Chọn **"Mail"** và **"Other (Custom name)"**
   - Nhập tên: `Render`
   - Click **"Generate"**
   - Copy password 16 ký tự (không có dấu cách)

2. **Thêm Environment Variables:**

```
Key: MAIL_HOST
Value: smtp.gmail.com
```

```
Key: MAIL_PORT
Value: 587
```

```
Key: MAIL_USERNAME
Value: your_email@gmail.com
```

```
Key: MAIL_PASSWORD
Value: your_gmail_app_password_16_chars
```

### 5.6. CORS (Tùy chọn - nếu có frontend)

Nếu bạn có frontend và cần gọi API:

```
Key: CORS_ALLOWED_ORIGINS
Value: https://your-frontend-domain.com
```

**Nếu có nhiều origins:**
```
Key: CORS_ALLOWED_ORIGINS
Value: https://domain1.com,https://domain2.com
```

---

## 📋 Bước 6: Deploy

1. **Kiểm tra lại tất cả cấu hình:**
   - ✅ Runtime = Docker
   - ✅ Build Command = trống
   - ✅ Start Command = trống
   - ✅ Tất cả environment variables đã thêm

2. **Click "Create Web Service"** ở cuối trang

3. **Render sẽ bắt đầu build và deploy:**
   - Build Docker image (có thể mất 5-10 phút)
   - Deploy container
   - Start application

---

## 📋 Bước 7: Theo Dõi Quá Trình Deploy

### 7.1. Xem Build Logs

Sau khi click "Create Web Service", bạn sẽ thấy:
- Tab **"Logs"** hiển thị quá trình build
- Build sẽ mất khoảng 5-10 phút

**Các log quan trọng cần chú ý:**
- ✅ `Successfully built` - Docker image build thành công
- ✅ `Starting application` - Application đang start
- ✅ `Started MycodeApplication` - Application đã start thành công
- ✅ `=== Hoàn thành khởi tạo dữ liệu ban đầu ===` - DataInitializer đã tạo dữ liệu

### 7.2. Kiểm Tra Lỗi

Nếu có lỗi, bạn sẽ thấy trong logs:
- ❌ `Connection refused` → Kiểm tra database URL và credentials
- ❌ `JWT secret must be at least 256 bits` → Kiểm tra JWT_SECRET có đủ 32 ký tự
- ❌ `Application run failed` → Xem logs chi tiết để tìm nguyên nhân

---

## 📋 Bước 8: Kiểm Tra Sau Khi Deploy

### 8.1. Kiểm Tra Health Check

Sau khi deploy thành công, URL sẽ có dạng:
```
https://ba-datn-backend.onrender.com
```

**Test health check:**
```bash
curl https://ba-datn-backend.onrender.com/actuator/health
```

**Kết quả mong đợi:**
```json
{
  "status": "UP"
}
```

### 8.2. Kiểm Tra Database

Kết nối database và kiểm tra:

```bash
PGPASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH psql -h dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com -U ba_datn_db_user -d ba_datn_db
```

**Kiểm tra dữ liệu đã được tạo:**
```sql
-- Xem chức vụ
SELECT * FROM chuc_vu;

-- Xem tài khoản admin
SELECT id, ma, ho_ten, email, tai_khoan, trang_thai 
FROM nhan_vien 
WHERE tai_khoan = 'admin';

-- Xem thương hiệu
SELECT * FROM thuong_hieu;

-- Xem màu sắc
SELECT * FROM mau_sac;
```

### 8.3. Test API

**Test login với tài khoản admin:**
```bash
curl -X POST https://ba-datn-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Kết quả mong đợi:**
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    "username": "admin",
    "email": "admin@datn.com"
  }
}
```

---

## 📋 Bước 9: Setup UptimeRobot (Khuyến nghị cho Free Tier)

Render Free Tier sẽ sleep sau 15 phút không có request. Để tránh điều này:

1. **Tạo tài khoản UptimeRobot:**
   - Vào: https://uptimerobot.com
   - Đăng ký miễn phí

2. **Tạo Monitor:**
   - Click **"Add New Monitor"**
   - **Monitor Type**: `HTTP(s)`
   - **Friendly Name**: `BA DATN Backend`
   - **URL**: `https://ba-datn-backend.onrender.com/actuator/health`
   - **Monitoring Interval**: `5 minutes`
   - Click **"Create Monitor"**

3. **Kết quả:**
   - UptimeRobot sẽ ping URL mỗi 5 phút
   - Service sẽ không bị sleep
   - Bạn sẽ nhận được alert nếu service down

---

## ✅ Checklist Hoàn Thành

Sau khi hoàn thành tất cả các bước:

- [ ] JWT secret key đã được tạo
- [ ] Web Service đã được tạo trên Render
- [ ] Runtime = Docker
- [ ] Tất cả environment variables đã được thêm
- [ ] Deploy thành công
- [ ] Health check trả về `{"status": "UP"}`
- [ ] Database có dữ liệu (chức vụ, admin user, v.v.)
- [ ] API login hoạt động
- [ ] UptimeRobot đã được setup (nếu dùng Free Tier)

---

## 🔧 Troubleshooting

### Lỗi: "JAVA_HOME is not defined"
**Nguyên nhân**: Runtime không phải Docker

**Giải pháp**:
1. Vào **Settings** của Web Service
2. Đổi **Runtime** từ `Node` hoặc `Java` sang `Docker`
3. Xóa **Build Command** và **Start Command** (để trống)
4. Save và deploy lại

### Lỗi: "Connection refused" hoặc "Database connection failed"
**Nguyên nhân**: Database URL hoặc credentials không đúng

**Giải pháp**:
1. Kiểm tra `SPRING_DATASOURCE_URL` có đúng format không
2. Kiểm tra username và password
3. Đảm bảo sử dụng **External Database URL** (có domain `.oregon-postgres.render.com`)
4. Kiểm tra database đang chạy (status = "Available")

### Lỗi: "JWT secret must be at least 256 bits"
**Nguyên nhân**: JWT_SECRET quá ngắn

**Giải pháp**:
1. Tạo lại JWT secret: `openssl rand -base64 32`
2. Update `JWT_SECRET` trong Environment Variables
3. Deploy lại

### Lỗi: "Application failed to start"
**Giải pháp**:
1. Xem logs chi tiết trong tab **"Logs"**
2. Kiểm tra tất cả environment variables đã set đủ chưa
3. Kiểm tra JWT_SECRET đã được set
4. Kiểm tra database connection

### Service Sleep (Free Tier)
**Vấn đề**: Service sẽ sleep sau 15 phút không có request

**Giải pháp**:
- Setup UptimeRobot (xem Bước 9)
- Hoặc upgrade lên paid plan

---

## 🎉 Hoàn Thành!

Sau khi hoàn thành tất cả các bước, bạn sẽ có:

- ✅ Backend API chạy trên Render
- ✅ Kết nối với PostgreSQL database
- ✅ Database schema được tạo tự động
- ✅ Dữ liệu mẫu được tạo tự động (DataInitializer)
- ✅ Tài khoản admin: `admin` / `admin123`
- ✅ HTTPS tự động
- ✅ Auto-deploy khi push code (nếu bật)

**Chúc bạn deploy thành công! 🚀**

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
1. Logs trên Render Dashboard
2. [HUONG_DAN_RENDER_CHI_TIET.md](./HUONG_DAN_RENDER_CHI_TIET.md) - Hướng dẫn chi tiết hơn
3. [DEPLOY_CHECKLIST.md](./DEPLOY_CHECKLIST.md) - Checklist đầy đủ
4. [RENDER_DEPLOY_GUIDE.md](./RENDER_DEPLOY_GUIDE.md) - Hướng dẫn tổng quan

