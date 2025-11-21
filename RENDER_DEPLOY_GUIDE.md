# 🚀 Hướng Dẫn Deploy Lên Render.com với Docker Runtime

Hướng dẫn chi tiết deploy ứng dụng Spring Boot lên Render.com sử dụng Docker runtime và PostgreSQL database.

## 📋 Thông Tin Database

Database PostgreSQL đã được tạo trên Render:

- **Hostname**: `dpg-d4emvkshg0os738ftv4g-a`
- **Port**: `5432`
- **Database**: `ba_datn_db`
- **Username**: `ba_datn_db_user`
- **Password**: `lNxER7uBbNYw60imnHx6coVCKyiUvSDH`
- **Internal Database URL**: `postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a/ba_datn_db`
- **External Database URL**: `postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com/ba_datn_db`

## 🐳 Cấu Hình Docker Runtime

### Bước 1: Đảm Bảo File Cấu Hình Đã Sẵn Sàng

✅ File `Dockerfile` đã có trong project  
✅ File `render.yaml` đã được cấu hình với `runtime: docker`  
✅ PostgreSQL dependency đã được thêm vào `pom.xml`

### Bước 2: Tạo Web Service Trên Render

1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"Web Service"**
3. Connect GitHub repository của bạn
4. Chọn repository và branch (thường là `main` hoặc `master`)

### Bước 3: Cấu Hình Runtime

**QUAN TRỌNG**: Phải chọn **Docker** runtime!

- **Runtime**: Chọn `Docker` từ dropdown
- **Build Command**: **Để trống** (Render tự động build từ Dockerfile)
- **Start Command**: **Để trống** (Render tự động chạy từ Dockerfile)
- **Plan**: Chọn `Free` (hoặc paid plan nếu có)

✅ **Ưu điểm của Docker**:
- Không cần lo về JAVA_HOME
- Môi trường build và runtime nhất quán
- Dễ dàng test local: `docker build -t app . && docker run -p 8080:8080 app`

### Bước 4: Cấu Hình Environment Variables

Vào tab **"Environment"** và thêm các biến sau:

#### Database (Bắt buộc)

```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db
SPRING_DATASOURCE_USERNAME=ba_datn_db_user
SPRING_DATASOURCE_PASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

**Lưu ý**: 
- Sử dụng **External Database URL** cho `SPRING_DATASOURCE_URL` vì Web Service và Database có thể ở các network khác nhau
- Format JDBC URL: `jdbc:postgresql://hostname:port/database`

#### JWT (Bắt buộc)

Tạo JWT secret key trước:

```bash
# Trên Linux/Mac
openssl rand -base64 32

# Hoặc sử dụng online generator
# https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
```

Sau đó thêm:

```
JWT_SECRET=your-generated-secret-key-here-minimum-32-characters
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

#### Spring Profile

```
SPRING_PROFILES_ACTIVE=prod
```

#### Port

```
PORT=8080
```

**Lưu ý**: Render tự động inject biến `PORT`, nhưng có thể set thủ công.

#### Email (Nếu có chức năng gửi email)

```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

**Lưu ý**: 
- Cần tạo Gmail App Password: https://myaccount.google.com/apppasswords
- Phải bật 2-Step Verification trước

#### CORS (Nếu có frontend)

```
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

**Lưu ý**: 
- Có thể thêm nhiều origins, cách nhau bởi dấu phẩy
- Format: `https://domain1.com,https://domain2.com`

### Bước 5: Deploy

1. Click **"Create Web Service"**
2. Render sẽ tự động:
   - Build Docker image từ Dockerfile
   - Deploy container
   - Start application
3. Xem logs trong tab **"Logs"** để theo dõi quá trình deploy
4. URL sẽ có dạng: `https://ba-datn-backend.onrender.com`

## 🔍 Kiểm Tra Deploy

### 1. Xem Logs

Vào tab **"Logs"** trên Render Dashboard để xem:
- Build logs
- Application startup logs
- Runtime logs

### 2. Health Check

Sau khi deploy thành công, kiểm tra health endpoint:

```bash
curl https://ba-datn-backend.onrender.com/actuator/health
```

Kết quả mong đợi:
```json
{
  "status": "UP"
}
```

### 3. Test API

Test một endpoint đơn giản:

```bash
curl https://ba-datn-backend.onrender.com/api/health
```

## ❌ Troubleshooting

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

### Lỗi: "Port already in use"

**Giải pháp**:
- Đảm bảo `application-prod.properties` có: `server.port=${PORT:8080}`
- Render tự động inject biến `PORT`, không cần set cứng

### Service Sleep (Free Tier)

**Vấn đề**: Service sẽ sleep sau 15 phút không có request.

**Giải pháp**:
1. Sử dụng **UptimeRobot** (miễn phí): https://uptimerobot.com
   - Tạo monitor ping URL mỗi 5 phút
   - URL: `https://ba-datn-backend.onrender.com/actuator/health`
2. Hoặc upgrade lên paid plan

## 📝 Checklist Trước Khi Deploy

- [ ] Code đã được push lên GitHub
- [ ] File `Dockerfile` đã có trong repo
- [ ] File `render.yaml` đã được commit và push
- [ ] PostgreSQL dependency đã được thêm vào `pom.xml`
- [ ] Đã tạo JWT secret key
- [ ] Đã chuẩn bị Gmail App Password (nếu cần)
- [ ] Đã set tất cả environment variables trên Render Dashboard
- [ ] Đã chọn Docker runtime (không phải Node.js)
- [ ] Build Command và Start Command đã để trống

## 🔐 Security Best Practices

- ✅ **KHÔNG** commit thông tin nhạy cảm vào code
- ✅ Sử dụng environment variables cho tất cả secrets
- ✅ Sử dụng `application-prod.properties` với profile `prod`
- ✅ Tắt Swagger trong production (đã cấu hình trong `application-prod.properties`)
- ✅ Enable HTTPS (Render tự động có)
- ✅ JWT secret key phải ít nhất 256 bits (32 ký tự)

## 📚 Tài Liệu Tham Khảo

- [Render Documentation](https://render.com/docs)
- [Spring Boot Deployment](https://spring.io/guides/gs/spring-boot-for-azure/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)

## 🎉 Hoàn Thành!

Sau khi deploy thành công, bạn sẽ có:
- ✅ Backend API chạy trên Render
- ✅ Kết nối với PostgreSQL database
- ✅ HTTPS tự động
- ✅ Auto-deploy khi push code (nếu bật)

**Chúc bạn deploy thành công! 🚀**

