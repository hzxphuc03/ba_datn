# ✅ Checklist Deploy Lên Render.com

Checklist chi tiết để deploy ứng dụng Spring Boot lên Render.com với PostgreSQL database mới.

## 📋 Trước Khi Deploy

### 1. Chuẩn Bị Code
- [x] Code đã được push lên GitHub
- [x] File `Dockerfile` đã có trong repo
- [x] File `render.yaml` đã được commit và push
- [x] PostgreSQL dependency đã được thêm vào `pom.xml`
- [x] Spring Boot Actuator dependency đã được thêm vào `pom.xml`
- [x] `application-prod.properties` đã được cấu hình với `ddl-auto=update`

### 2. Chuẩn Bị Secrets
- [ ] Đã tạo JWT secret key (chạy: `openssl rand -base64 32`)
- [ ] Đã chuẩn bị Gmail App Password (nếu cần gửi email)
  - Bật 2-Step Verification: https://myaccount.google.com/security
  - Tạo App Password: https://myaccount.google.com/apppasswords
- [ ] Đã có frontend URL (nếu cần cấu hình CORS)

### 3. Database PostgreSQL
- [x] Database đã được tạo trên Render
- [x] Đã có thông tin kết nối database:
  - Hostname: `dpg-d4emvkshg0os738ftv4g-a`
  - Port: `5432`
  - Database: `ba_datn_db`
  - Username: `ba_datn_db_user`
  - Password: `lNxER7uBbNYw60imnHx6coVCKyiUvSDH`

---

## 🚀 Các Bước Deploy Trên Render

### Bước 1: Tạo Web Service
1. [ ] Đăng nhập vào [Render Dashboard](https://dashboard.render.com)
2. [ ] Click **"New +"** → **"Web Service"**
3. [ ] Connect GitHub repository: `hzxphuc03/ba_datn`
4. [ ] Chọn branch: `main` (hoặc branch bạn muốn deploy)

### Bước 2: Cấu Hình Runtime
- [ ] **Runtime**: Chọn `Docker` (QUAN TRỌNG!)
- [ ] **Build Command**: Để trống
- [ ] **Start Command**: Để trống
- [ ] **Plan**: Chọn `Free` (hoặc paid plan)

### Bước 3: Cấu Hình Environment Variables
Vào tab **"Environment"** và thêm các biến sau:

#### Database (Bắt buộc)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db
SPRING_DATASOURCE_USERNAME=ba_datn_db_user
SPRING_DATASOURCE_PASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### Spring Profile
```
SPRING_PROFILES_ACTIVE=prod
```

#### Port
```
PORT=8080
```

#### JWT (Bắt buộc)
```
JWT_SECRET=<paste-secret-key-đã-tạo-ở-trên>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

#### Email (Nếu có chức năng gửi email)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

#### CORS (Nếu có frontend)
```
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

**Lưu ý**: 
- Có thể thêm nhiều origins, cách nhau bởi dấu phẩy
- Format: `https://domain1.com,https://domain2.com`

### Bước 4: Deploy
- [ ] Click **"Create Web Service"**
- [ ] Chờ build và deploy (có thể mất 5-10 phút)
- [ ] Xem logs trong tab **"Logs"** để theo dõi quá trình

---

## ✅ Kiểm Tra Sau Khi Deploy

### 1. Kiểm Tra Build
- [ ] Build thành công (không có lỗi trong build logs)
- [ ] Docker image được build thành công

### 2. Kiểm Tra Application Startup
- [ ] Application start thành công
- [ ] Không có lỗi trong startup logs
- [ ] Database connection thành công
- [ ] Hibernate đã tạo schema (tables được tạo tự động)

### 3. Kiểm Tra Health Check
```bash
curl https://ba-datn-backend.onrender.com/actuator/health
```

Kết quả mong đợi:
```json
{
  "status": "UP"
}
```

- [ ] Health check trả về `{"status": "UP"}`

### 4. Kiểm Tra API
- [ ] Test một endpoint đơn giản (ví dụ: GET `/api/the_loai`)
- [ ] API trả về response đúng

### 5. Kiểm Tra Database
- [ ] Kết nối database thành công
- [ ] Tables đã được tạo tự động bởi Hibernate
- [ ] Có thể query dữ liệu từ database

---

## 🔧 Troubleshooting

### Lỗi: "JAVA_HOME is not defined"
**Nguyên nhân**: Render đang sử dụng Node.js runtime thay vì Docker runtime.

**Giải pháp**:
1. Vào **Settings** của Web Service
2. Đổi **Runtime** từ `Node` sang `Docker`
3. Xóa **Build Command** và **Start Command** (để trống)
4. Save và deploy lại

### Lỗi: "Connection refused" hoặc "Database connection failed"
**Nguyên nhân**: Database URL hoặc credentials không đúng.

**Giải pháp**:
1. Kiểm tra lại `SPRING_DATASOURCE_URL` có đúng format không
2. Kiểm tra username và password
3. Đảm bảo sử dụng **External Database URL** (có domain `.oregon-postgres.render.com`)
4. Kiểm tra database đã được tạo và đang chạy chưa

### Lỗi: "Application failed to start"
**Giải pháp**:
1. Xem logs chi tiết trong tab **"Logs"**
2. Kiểm tra tất cả environment variables đã set đủ chưa
3. Kiểm tra JWT_SECRET đã được set và có ít nhất 32 ký tự
4. Kiểm tra database connection

### Lỗi: "Schema validation failed"
**Nguyên nhân**: Database schema không khớp với entities.

**Giải pháp**:
1. Đảm bảo `ddl-auto=update` trong `application-prod.properties`
2. Xóa database và để Hibernate tạo lại schema
3. Hoặc chạy migration script thủ công

### Service Sleep (Free Tier)
**Vấn đề**: Service sẽ sleep sau 15 phút không có request.

**Giải pháp**:
1. Sử dụng **UptimeRobot** (miễn phí): https://uptimerobot.com
   - Tạo monitor ping URL mỗi 5 phút
   - URL: `https://ba-datn-backend.onrender.com/actuator/health`
2. Hoặc upgrade lên paid plan

---

## 📝 Sau Khi Deploy Thành Công

### 1. Đổi lại ddl-auto (Tùy chọn - Khuyến nghị)
Sau khi deploy thành công và có dữ liệu, nên đổi lại `ddl-auto=validate` để bảo mật hơn:

**File**: `src/main/resources/application-prod.properties`
```properties
spring.jpa.hibernate.ddl-auto=validate
```

Sau đó commit và push lại code.

### 2. Setup UptimeRobot (Khuyến nghị cho Free Tier)
- [ ] Tạo tài khoản UptimeRobot
- [ ] Tạo monitor mới
- [ ] URL: `https://ba-datn-backend.onrender.com/actuator/health`
- [ ] Interval: 5 phút
- [ ] Alert: Email hoặc SMS

### 3. Test Tất Cả Chức Năng
- [ ] Test authentication (login, register)
- [ ] Test các API endpoints
- [ ] Test email (nếu có)
- [ ] Test WebSocket (nếu có)

---

## 📚 Tài Liệu Tham Khảo

- [Render Documentation](https://render.com/docs)
- [Spring Boot Deployment](https://spring.io/guides/gs/spring-boot-for-azure/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)
- [RENDER_DEPLOY_GUIDE.md](./RENDER_DEPLOY_GUIDE.md) - Hướng dẫn chi tiết hơn

---

## 🎉 Hoàn Thành!

Sau khi hoàn thành tất cả các bước trên, bạn sẽ có:
- ✅ Backend API chạy trên Render
- ✅ Kết nối với PostgreSQL database
- ✅ Database schema được tạo tự động
- ✅ HTTPS tự động
- ✅ Auto-deploy khi push code (nếu bật)

**Chúc bạn deploy thành công! 🚀**

