# 🚀 Hướng Dẫn Deploy Spring Boot Lên Hosting Miễn Phí

Hướng dẫn chi tiết deploy ứng dụng Spring Boot lên các nền tảng hosting miễn phí phổ biến.

## 📋 Mục Lục

1. [Chuẩn Bị Trước Khi Deploy](#1-chuẩn-bị-trước-khi-deploy)
2. [Deploy Lên Railway.app](#2-deploy-lên-railwayapp)
3. [Deploy Lên Render.com](#3-deploy-lên-rendercom)
4. [Deploy Lên Fly.io](#4-deploy-lên-flyio)
5. [Cấu Hình Database](#5-cấu-hình-database)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. Chuẩn Bị Trước Khi Deploy

### 1.1. Checklist

- [ ] Code đã được push lên GitHub
- [ ] Đã test ứng dụng chạy thành công ở local
- [ ] Đã cấu hình environment variables
- [ ] Đã chuẩn bị thông tin database
- [ ] Đã có thông tin email (Gmail App Password)
- [ ] Đã có JWT secret key (nếu cần)

### 1.2. Cấu Hình Environment Variables

Ứng dụng cần các biến môi trường sau. **Lưu ý**: Tên biến có thể khác nhau tùy platform, xem chi tiết ở từng phần hướng dẫn.

**Template đầy đủ:**

```bash
# Database (PostgreSQL - recommended)
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Database (SQL Server - nếu giữ nguyên)
# SPRING_DATASOURCE_URL=jdbc:sqlserver://host:1433;databaseName=DB_NAME;encrypt=true;trustServerCertificate=true
# SPRING_DATASOURCE_USERNAME=sa
# SPRING_DATASOURCE_PASSWORD=your_password
# SPRING_DATASOURCE_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
# HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect

# JWT
JWT_SECRET=your-very-long-secret-key-at-least-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Server (PORT được tự động set bởi hosting platform)
PORT=8080

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

**Tạo file `.env.example`** (không commit file `.env` thực tế):
- Copy template trên vào file `.env.example`
- Điền giá trị thực vào file `.env` (local development)
- Trên hosting platform, set các biến này trong Environment Variables

### 1.3. Tạo JWT Secret Key

Tạo một secret key mạnh (ít nhất 256 bits):

```bash
# Trên Linux/Mac
openssl rand -base64 32

# Hoặc sử dụng online generator
# https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
```

### 1.4. Gmail App Password

Nếu sử dụng Gmail:

1. Vào Google Account → Security
2. Bật 2-Step Verification
3. Tạo App Password: https://myaccount.google.com/apppasswords
4. Copy password 16 ký tự (không có dấu cách)

---

## 2. Deploy Lên Railway.app

Railway là nền tảng dễ nhất để deploy Spring Boot, hỗ trợ tốt Java và có database PostgreSQL miễn phí.

### 2.1. Đăng Ký Tài Khoản

1. Truy cập: https://railway.app
2. Đăng nhập bằng GitHub
3. Xác nhận email

### 2.2. Tạo Project Mới

1. Click **"New Project"**
2. Chọn **"Deploy from GitHub repo"**
3. Chọn repository của bạn
4. Railway sẽ tự động detect Spring Boot project

### 2.3. Tạo PostgreSQL Database

1. Trong project, click **"+ New"**
2. Chọn **"Database"** → **"Add PostgreSQL"**
3. Railway sẽ tự động tạo database
4. Copy connection string từ tab **"Variables"**

### 2.4. Cấu Hình Environment Variables

1. Vào tab **"Variables"** của service
2. Thêm các biến sau:

```bash
# Database (từ PostgreSQL service)
DATABASE_URL=${{Postgres.DATABASE_URL}}
# Hoặc cấu hình thủ công:
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# JWT
JWT_SECRET=your-generated-secret-key-here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Profile
SPRING_PROFILES_ACTIVE=prod

# Server Port (Railway tự động set PORT)
PORT=8080

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### 2.5. Cấu Hình Build Settings

1. Vào tab **"Settings"**
2. **Build Command**: `./mvnw clean package -DskipTests`
3. **Start Command**: `java -jar target/mycode-0.0.1-SNAPSHOT.jar`
4. **Root Directory**: `/` (mặc định)

### 2.6. Deploy

1. Railway sẽ tự động deploy khi bạn push code
2. Xem logs trong tab **"Deployments"**
3. URL sẽ có dạng: `https://your-app-name.up.railway.app`

### 2.7. Cấu Hình Custom Domain (Tùy chọn)

1. Vào **"Settings"** → **"Networking"**
2. Click **"Generate Domain"** hoặc **"Add Custom Domain"**
3. Cấu hình DNS theo hướng dẫn

---

## 3. Deploy Lên Render.com

Render cung cấp free tier với PostgreSQL, nhưng service sẽ sleep sau 15 phút không dùng.

### 3.1. Đăng Ký Tài Khoản

1. Truy cập: https://render.com
2. Đăng nhập bằng GitHub
3. Xác nhận email

### 3.2. Tạo PostgreSQL Database

1. Dashboard → **"New +"** → **"PostgreSQL"**
2. Đặt tên database
3. Chọn **"Free"** plan
4. Chọn region (Singapore gần VN nhất)
5. Click **"Create Database"**
6. Copy **Internal Database URL** và **External Database URL**

### 3.3. Tạo Web Service

1. Dashboard → **"New +"** → **"Web Service"**
2. Connect GitHub repository
3. Chọn repository và branch

### 3.4. Cấu Hình Build & Deploy

**Cách 1: Sử dụng file `render.yaml` (Khuyến nghị)**

File `render.yaml` đã được tạo trong project để Render tự động detect Java runtime:

1. Đảm bảo file `render.yaml` đã có trong repo (đã commit và push)
2. Khi tạo Web Service, Render sẽ tự động đọc file này
3. **Runtime** sẽ tự động được set thành `Java`
4. **Build Command** và **Start Command** sẽ được tự động cấu hình

**Cách 2: Cấu hình thủ công trong Dashboard**

Nếu không dùng `render.yaml`, cấu hình như sau:

**Basic Settings:**
- **Name**: `ba-datn-backend` (hoặc tên bạn muốn)
- **Region**: Singapore
- **Branch**: `main`
- **Root Directory**: `/` (mặc định)
- **Runtime**: `Java` ⚠️ **QUAN TRỌNG**: Phải chọn Java, không phải Node.js!
- **Build Command**: `chmod +x mvnw && ./mvnw clean package -DskipTests`
- **Start Command**: `java -jar target/mycode-0.0.1-SNAPSHOT.jar`

**Lưu ý**: 
- Nếu gặp lỗi "Permission denied", build command đã có `chmod +x mvnw`
- Nếu gặp lỗi "JAVA_HOME is not defined", đảm bảo **Runtime** được set thành `Java` (không phải Node.js)

**Advanced Settings:**
- **Auto-Deploy**: `Yes` (tự động deploy khi push code)

### 3.5. Cấu Hình Environment Variables

Trong tab **"Environment"**, thêm:

```bash
# Database (sử dụng Internal Database URL từ PostgreSQL service)
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname
SPRING_DATASOURCE_USERNAME=render
SPRING_DATASOURCE_PASSWORD=password

# Hoặc sử dụng DATABASE_URL nếu Render tự động inject
# SPRING_DATASOURCE_URL=${DATABASE_URL}

# JWT
JWT_SECRET=your-generated-secret-key-here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Profile
SPRING_PROFILES_ACTIVE=prod

# Port (Render tự động set PORT)
PORT=8080

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### 3.6. Deploy

1. Click **"Create Web Service"**
2. Render sẽ tự động build và deploy
3. Xem logs trong tab **"Logs"**
4. URL: `https://your-app-name.onrender.com`

### 3.7. Lưu Ý Free Tier

- Service sẽ **sleep sau 15 phút** không có request
- Request đầu tiên sau khi sleep sẽ mất **30-60 giây** để wake up
- Có thể dùng **UptimeRobot** (miễn phí) để ping mỗi 5 phút

---

## 4. Deploy Lên Fly.io

Fly.io cung cấp 3 VMs miễn phí, hiệu năng tốt nhưng cần Dockerfile.

### 4.1. Cài Đặt Fly CLI

```bash
# Mac
curl -L https://fly.io/install.sh | sh

# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex

# Linux
curl -L https://fly.io/install.sh | sh
```

### 4.2. Đăng Nhập

```bash
fly auth login
```

### 4.3. Tạo Fly App

```bash
cd /path/to/your/project
fly launch
```

Fly sẽ hỏi:
- App name: `ba-datn-backend` (hoặc tên bạn muốn)
- Region: `sin` (Singapore) hoặc `nrt` (Tokyo)
- PostgreSQL: Chọn **"Yes"** để tạo database

### 4.4. Cấu Hình fly.toml

File `fly.toml` sẽ được tạo tự động. Cập nhật:

```toml
app = "ba-datn-backend"
primary_region = "sin"

[build]

[env]
  SPRING_PROFILES_ACTIVE = "prod"
  PORT = "8080"

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = false
  auto_start_machines = true
  min_machines_running = 1

[[services]]
  protocol = "tcp"
  internal_port = 8080
```

### 4.5. Cấu Hình Secrets (Environment Variables)

```bash
# JWT
fly secrets set JWT_SECRET="your-generated-secret-key-here"
fly secrets set JWT_EXPIRATION="86400000"
fly secrets set JWT_REFRESH_EXPIRATION="604800000"

# Email
fly secrets set MAIL_USERNAME="your_email@gmail.com"
fly secrets set MAIL_PASSWORD="your_app_password"

# CORS
fly secrets set CORS_ALLOWED_ORIGINS="https://your-frontend.com"
```

### 4.6. Cấu Hình Database Connection

Nếu đã tạo PostgreSQL:

```bash
# Xem database URL
fly postgres connect -a ba-datn-backend-db

# Set database URL
fly secrets set SPRING_DATASOURCE_URL="jdbc:postgresql://host:port/dbname"
fly secrets set SPRING_DATASOURCE_USERNAME="postgres"
fly secrets set SPRING_DATASOURCE_PASSWORD="password"
```

### 4.7. Deploy

```bash
fly deploy
```

### 4.8. Xem Logs

```bash
fly logs
```

### 4.9. Mở App

```bash
fly open
```

URL sẽ có dạng: `https://ba-datn-backend.fly.dev`

---

## 5. Cấu Hình Database

### 5.1. Chuyển Từ SQL Server Sang PostgreSQL

Nếu bạn đang dùng SQL Server và muốn chuyển sang PostgreSQL (miễn phí trên Railway/Render):

#### 5.1.1. Thêm Dependency PostgreSQL

Trong `pom.xml`, thêm dependency sau vào phần `<dependencies>`:

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Lưu ý**: Bạn có thể giữ cả SQL Server và PostgreSQL dependencies, Spring Boot sẽ tự động chọn driver phù hợp dựa trên connection string.

#### 5.1.2. Cập Nhật application.properties

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://host:port/dbname
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### 5.1.3. Migration Data

1. Export data từ SQL Server
2. Convert schema nếu cần (một số types khác nhau)
3. Import vào PostgreSQL

### 5.2. Giữ Nguyên SQL Server

Nếu muốn giữ SQL Server, bạn cần:

1. **Azure SQL Database** (có free tier 12 tháng)
2. **AWS RDS** (free tier 12 tháng)
3. **Hosting riêng** SQL Server

Cấu hình connection string:

```properties
spring.datasource.url=jdbc:sqlserver://host:port;databaseName=DB_NAME;encrypt=true;trustServerCertificate=false
spring.datasource.username=username
spring.datasource.password=password
```

---

## 6. Troubleshooting

### 6.1. Build Failed

**Lỗi**: `Maven not found` hoặc `Java not found` hoặc `Permission denied` hoặc `JAVA_HOME is not defined`

**Giải pháp**:

1. **Permission denied với mvnw**: 
   - Thêm `chmod +x mvnw &&` vào đầu build command:
     ```bash
     chmod +x mvnw && ./mvnw clean package -DskipTests
     ```
   - Hoặc chạy lệnh sau và commit:
     ```bash
     git update-index --chmod=+x mvnw
     git commit -m "Add executable permission to mvnw"
     git push
     ```

2. **JAVA_HOME is not defined (Render)**:
   - ⚠️ **Nguyên nhân chính**: Render đang sử dụng **Node.js runtime** thay vì **Java runtime**
   - **Giải pháp 1 (Khuyến nghị)**: Sử dụng file `render.yaml`
     - Đảm bảo file `render.yaml` đã có trong repo và đã được commit/push
     - Khi tạo Web Service mới, Render sẽ tự động detect Java runtime từ file này
   - **Giải pháp 2**: Cấu hình thủ công trong Render Dashboard
     - Vào **Settings** của Web Service
     - Tìm phần **Runtime** và đổi từ `Node` sang `Java`
     - Lưu và deploy lại
   - **Giải pháp 3**: Xóa service cũ và tạo lại với Runtime = Java ngay từ đầu

3. **Railway/Render**: Đảm bảo có `mvnw` hoặc `mvnw.cmd` trong project

4. **Fly.io**: Kiểm tra Dockerfile có cài đặt Maven đúng chưa

### 6.2. Application Không Start

**Lỗi**: `Application failed to start`

**Kiểm tra**:
1. Xem logs chi tiết
2. Kiểm tra database connection
3. Kiểm tra environment variables đã set đủ chưa
4. Kiểm tra port: Railway/Render/Fly tự động inject `PORT`, đảm bảo app đọc biến này

**Fix Port**:
Trong `application.properties`:
```properties
server.port=${PORT:8080}
```

### 6.3. Database Connection Error

**Lỗi**: `Connection refused` hoặc `Authentication failed`

**Giải pháp**:
1. Kiểm tra database URL đúng chưa
2. Kiểm tra username/password
3. Với Railway/Render: Sử dụng **Internal Database URL** (không phải External)
4. Kiểm tra firewall/network settings

### 6.4. JWT Token Invalid

**Lỗi**: Token không được validate

**Giải pháp**:
1. Đảm bảo `JWT_SECRET` đã được set và giống nhau giữa các lần deploy
2. Secret key phải ít nhất 256 bits (32 ký tự)
3. Không thay đổi secret key sau khi đã có users đăng nhập

### 6.5. CORS Error

**Lỗi**: `Access-Control-Allow-Origin` error

**Giải pháp**:
1. Cập nhật `WebConfig.java` để đọc từ environment variable
2. Set `CORS_ALLOWED_ORIGINS` trong hosting platform
3. Đảm bảo frontend URL đúng format (có https://)

### 6.6. Email Không Gửi Được

**Lỗi**: Email service failed

**Giải pháp**:
1. Kiểm tra Gmail App Password đúng chưa
2. Đảm bảo đã bật 2-Step Verification
3. Kiểm tra `MAIL_USERNAME` và `MAIL_PASSWORD` đã set đúng

### 6.7. Service Sleep (Render)

**Vấn đề**: Service bị sleep sau 15 phút

**Giải pháp**:
1. Sử dụng **UptimeRobot** (miễn phí): https://uptimerobot.com
   - Tạo monitor ping URL mỗi 5 phút
2. Hoặc upgrade lên paid plan

### 6.8. Out of Memory

**Lỗi**: `OutOfMemoryError`

**Giải pháp**:
1. Tăng memory limit trong hosting settings
2. Hoặc optimize JVM args:
```bash
JAVA_OPTS=-Xmx512m -Xms256m
```

### 6.9. Xem Logs

**Railway**:
- Tab "Deployments" → Click vào deployment → Xem logs

**Render**:
- Tab "Logs" → Real-time logs

**Fly.io**:
```bash
fly logs
# Hoặc follow logs
fly logs -a ba-datn-backend
```

---

## 7. Best Practices

### 7.1. Security

- ✅ **KHÔNG** commit `application.properties` có thông tin nhạy cảm
- ✅ Sử dụng environment variables cho tất cả secrets
- ✅ Sử dụng `application-prod.properties` với profile `prod`
- ✅ Tắt Swagger trong production
- ✅ Enable HTTPS (Railway/Render/Fly tự động có)

### 7.2. Performance

- ✅ Sử dụng connection pooling (HikariCP mặc định)
- ✅ Tắt debug logging trong production
- ✅ Optimize JPA queries
- ✅ Cache static resources

### 7.3. Monitoring

- ✅ Setup health check endpoint: `/actuator/health`
- ✅ Monitor logs thường xuyên
- ✅ Setup alerts cho errors

---

## 8. So Sánh Các Nền Tảng

| Tính năng | Railway | Render | Fly.io |
|-----------|---------|--------|--------|
| **Free Tier** | $5 credit/tháng | Free (có giới hạn) | 3 VMs free |
| **Database** | PostgreSQL miễn phí | PostgreSQL miễn phí | PostgreSQL (trả phí) |
| **Auto Deploy** | ✅ | ✅ | ✅ |
| **Sleep Mode** | ❌ | ✅ (15 phút) | ❌ |
| **Custom Domain** | ✅ | ✅ | ✅ |
| **Dockerfile** | Không cần | Không cần | Cần |
| **Độ khó** | ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó hơn |
| **Tốc độ** | ⭐⭐⭐ Nhanh | ⭐⭐ Trung bình | ⭐⭐⭐ Rất nhanh |

**Khuyến nghị**:
- **Người mới**: Railway
- **Cần free tier ổn định**: Render (với UptimeRobot)
- **Cần hiệu năng cao**: Fly.io

---

## 9. Liên Kết Hữu Ích

- Railway Docs: https://docs.railway.app
- Render Docs: https://render.com/docs
- Fly.io Docs: https://fly.io/docs
- Spring Boot Deployment: https://spring.io/guides/gs/spring-boot-for-azure/
- PostgreSQL Migration: https://www.postgresql.org/docs/current/migration.html

---

## 10. Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra logs chi tiết
2. Xem documentation của platform
3. Search trên Stack Overflow
4. Tạo issue trên GitHub repo

**Chúc bạn deploy thành công! 🎉**

