# 🚀 Hướng Dẫn Deploy lên Render

## 📋 Yêu Cầu

1. Tài khoản Render (https://render.com)
2. GitHub repository đã push code
3. PostgreSQL database trên Render (hoặc external)

## 🔧 Các Bước Deploy

### Bước 1: Tạo PostgreSQL Database trên Render

1. Đăng nhập vào Render Dashboard
2. Click **"New +"** → **"PostgreSQL"**
3. Điền thông tin:
   - **Name**: `datn-db` (hoặc tên bạn muốn)
   - **Database**: `ba_datn_db`
   - **User**: `ba_datn_db_user`
   - **Region**: Singapore (hoặc gần nhất)
   - **Plan**: Free (hoặc paid)
4. Click **"Create Database"**
5. Lưu lại:
   - **Internal Database URL** (dùng trong Render)
   - **External Database URL** (nếu cần kết nối từ ngoài)

### Bước 2: Tạo Web Service trên Render

1. Click **"New +"** → **"Web Service"**
2. Connect GitHub repository của bạn
3. Chọn repository và branch (`testing`)
4. Điền thông tin:
   - **Name**: `datn-backend`
   - **Region**: Singapore
   - **Branch**: `testing`
   - **Root Directory**: (để trống)
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `Dockerfile`
   - **Docker Context**: `.`
   - **Plan**: Free (hoặc paid)

### Bước 3: Cấu Hình Environment Variables

Trong phần **Environment** của Web Service, thêm các biến sau:

#### Database Configuration
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/ba_datn_db
SPRING_DATASOURCE_USERNAME=ba_datn_db_user
SPRING_DATASOURCE_PASSWORD=your_password_here
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
```

#### JWT Configuration (QUAN TRỌNG)
```bash
JWT_SECRET=your-very-strong-secret-key-at-least-32-characters-long-2024
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

**⚠️ LƯU Ý**: JWT_SECRET phải:
- Ít nhất 32 ký tự
- Không được commit lên Git
- Phải giống nhau giữa các lần deploy

#### CORS Configuration (QUAN TRỌNG)
```bash
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

Hoặc nếu đang test:
```bash
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://yourdomain.com
```

#### Email Configuration (Optional)
```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

#### Spring Configuration
```bash
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
PORT=8080
```

### Bước 4: Cấu Hình Build & Deploy

1. **Build Command**: (để trống - Docker sẽ tự build)
2. **Start Command**: (để trống - Dockerfile đã có ENTRYPOINT)
3. **Health Check Path**: `/actuator/health`

### Bước 5: Deploy

1. Click **"Create Web Service"**
2. Render sẽ tự động:
   - Build Docker image
   - Deploy container
   - Start service
3. Chờ build hoàn tất (5-10 phút lần đầu)
4. Kiểm tra logs để đảm bảo không có lỗi

## 🔍 Kiểm Tra Deploy

### 1. Health Check
```bash
curl https://your-service.onrender.com/actuator/health
```

Kết quả mong đợi:
```json
{"status":"UP"}
```

### 2. Test Login Endpoint
```bash
curl -X POST https://your-service.onrender.com/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username":"test","pass":"test"}'
```

### 3. Kiểm Tra Logs

Trong Render Dashboard:
- Vào **"Logs"** tab
- Kiểm tra:
  - ✅ Application started successfully
  - ✅ Database connection successful
  - ❌ Không có lỗi JWT hoặc CORS

## 🐛 Troubleshooting

### Lỗi: Unable to access jarfile /app.jar

**Nguyên nhân**: JAR file không được tạo hoặc copy sai

**Giải pháp**:
1. Kiểm tra build logs xem JAR có được tạo không
2. Đảm bảo Dockerfile đúng
3. Kiểm tra `pom.xml` có cấu hình Spring Boot Maven plugin

### Lỗi: JWT validation failed

**Nguyên nhân**: JWT_SECRET không được set hoặc khác với local

**Giải pháp**:
1. Kiểm tra environment variable `JWT_SECRET` đã set chưa
2. Đảm bảo JWT_SECRET ít nhất 32 ký tự
3. Restart service sau khi set JWT_SECRET

### Lỗi: CORS policy blocked

**Nguyên nhân**: CORS_ALLOWED_ORIGINS không đúng

**Giải pháp**:
1. Kiểm tra `CORS_ALLOWED_ORIGINS` đã set domain frontend chưa
2. Format đúng: `https://domain1.com,https://domain2.com`
3. Không có khoảng trắng thừa

### Lỗi: Database connection failed

**Nguyên nhân**: Database URL hoặc credentials sai

**Giải pháp**:
1. Kiểm tra `SPRING_DATASOURCE_URL` đúng format PostgreSQL
2. Kiểm tra username/password
3. Đảm bảo database đã được tạo và running

### Lỗi: Port already in use

**Nguyên nhân**: PORT environment variable conflict

**Giải pháp**:
1. Render tự động inject PORT, không cần set
2. Nếu vẫn lỗi, xóa PORT environment variable

## 📝 Checklist Trước Khi Deploy

- [ ] Code đã push lên GitHub
- [ ] Dockerfile đã được test local
- [ ] PostgreSQL database đã được tạo
- [ ] Environment variables đã được set:
  - [ ] SPRING_DATASOURCE_URL
  - [ ] SPRING_DATASOURCE_USERNAME
  - [ ] SPRING_DATASOURCE_PASSWORD
  - [ ] JWT_SECRET (ít nhất 32 ký tự)
  - [ ] CORS_ALLOWED_ORIGINS
- [ ] application-prod.properties đã cấu hình đúng
- [ ] Health check endpoint hoạt động

## 🔐 Security Checklist

- [ ] JWT_SECRET mạnh và không commit lên Git
- [ ] Database password mạnh
- [ ] CORS chỉ allow domain production
- [ ] Swagger đã tắt trong production
- [ ] Debug logging đã tắt
- [ ] HTTPS enabled (Render tự động)

## 📚 Tài Liệu Tham Khảo

- [Render Documentation](https://render.com/docs)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL on Render](https://render.com/docs/databases)

## 🆘 Support

Nếu gặp vấn đề:
1. Kiểm tra logs trong Render Dashboard
2. Test local với Docker trước
3. Kiểm tra environment variables
4. Xem troubleshooting section ở trên

---

**Lưu ý**: Free plan trên Render sẽ sleep sau 15 phút không có traffic. Để tránh sleep, có thể:
- Upgrade lên paid plan
- Sử dụng service như UptimeRobot để ping định kỳ
- Hoặc chấp nhận cold start (30-60 giây lần đầu sau khi sleep)

