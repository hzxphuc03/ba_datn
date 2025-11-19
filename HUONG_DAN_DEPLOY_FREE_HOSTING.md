# 🚀 Hướng Dẫn Deploy Spring Boot Lên Hosting Miễn Phí

Hướng dẫn chi tiết deploy ứng dụng Spring Boot lên các nền tảng hosting miễn phí phổ biến.

## 📋 Mục Lục

1. [Chuẩn Bị Trước Khi Deploy](#1-chuẩn-bị-trước-khi-deploy)
2. [Deploy Lên Railway.app](#2-deploy-lên-railwayapp) ⭐ **KHUYẾN NGHỊ**
3. [Deploy Lên Koyeb](#3-deploy-lên-koyeb) ⭐ **DỄ NHẤT**
4. [Deploy Lên Oracle Cloud](#4-deploy-lên-oracle-cloud-free-tier)
5. [Deploy Lên Fly.io](#5-deploy-lên-flyio)
6. [Deploy Lên Render.com](#6-deploy-lên-rendercom) ⚠️ Không khuyến nghị
7. [Cấu Hình Database](#7-cấu-hình-database)
8. [Troubleshooting](#8-troubleshooting)

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

## 3. Deploy Lên Koyeb ⭐ DỄ NHẤT

Koyeb là nền tảng rất dễ sử dụng, hỗ trợ Java/Spring Boot tốt, không cần cấu hình phức tạp.

### 3.1. Đăng Ký Tài Khoản

1. Truy cập: https://www.koyeb.com
2. Đăng nhập bằng GitHub
3. Xác nhận email

### 3.2. Tạo App

1. Dashboard → **"Create App"**
2. Chọn **"GitHub"** → Chọn repository `ba_datn`
3. Chọn branch `main`

### 3.3. Cấu Hình Build & Deploy

**Build Settings:**
- **Build Command**: `./mvnw clean package -DskipTests`
- **Run Command**: `java -jar target/mycode-0.0.1-SNAPSHOT.jar`
- **Type**: `Web Service`

Koyeb sẽ tự động detect Java và cấu hình.

### 3.4. Cấu Hình Environment Variables

Vào **"Variables"** tab, thêm:

```bash
# Database
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

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### 3.5. Tạo Database (PostgreSQL)

1. Dashboard → **"Databases"** → **"Create Database"**
2. Chọn **"PostgreSQL"**
3. Chọn **"Free"** plan
4. Copy connection string và thêm vào Environment Variables

### 3.6. Deploy

1. Click **"Deploy"**
2. Koyeb sẽ tự động build và deploy
3. URL: `https://your-app-name.koyeb.app`

**Ưu điểm Koyeb:**
- ✅ Rất dễ sử dụng, không cần cấu hình phức tạp
- ✅ Hỗ trợ Java/Spring Boot tốt
- ✅ Auto-deploy từ GitHub
- ✅ Free tier ổn định
- ✅ Không sleep như Render

---

## 4. Deploy Lên Oracle Cloud Free Tier

Oracle Cloud cung cấp 2 VMs miễn phí vĩnh viễn, mỗi VM có 1GB RAM - đủ để chạy Spring Boot.

### 4.1. Đăng Ký Tài Khoản

1. Truy cập: https://www.oracle.com/cloud/free/
2. Đăng ký tài khoản (cần thẻ tín dụng để xác minh, nhưng không bị charge)
3. Xác nhận email và đăng nhập

### 4.2. Tạo VM Instance

1. Vào **"Compute"** → **"Instances"**
2. Click **"Create Instance"**
3. Cấu hình:
   - **Name**: `ba-datn-backend`
   - **Image**: **Canonical Ubuntu 22.04** (hoặc Oracle Linux)
   - **Shape**: **VM.Standard.A1.Flex** (Free tier)
   - **OCPU**: 1
   - **Memory**: 1GB
   - **SSH Keys**: Upload public key hoặc tạo mới
4. Click **"Create"**

### 4.3. Cấu Hình Firewall

1. Vào **"Networking"** → **"Security Lists"**
2. Thêm **Ingress Rule**:
   - **Source**: `0.0.0.0/0`
   - **IP Protocol**: TCP
   - **Destination Port Range**: `8080`
   - **Description**: Spring Boot App

### 4.4. SSH vào VM và Cài Đặt Java

```bash
# SSH vào VM (thay IP bằng public IP của VM)
ssh ubuntu@<VM_PUBLIC_IP>

# Update system
sudo apt update && sudo apt upgrade -y

# Cài đặt Java 17
sudo apt install openjdk-17-jdk -y

# Cài đặt Maven (hoặc sử dụng mvnw từ project)
sudo apt install maven -y

# Verify
java -version
mvn -version
```

### 4.5. Clone và Deploy Code

```bash
# Clone repository
git clone https://github.com/hzxphuc03/ba_datn.git
cd ba_datn

# Build
chmod +x mvnw
./mvnw clean package -DskipTests

# Tạo systemd service
sudo nano /etc/systemd/system/ba-datn.service
```

Nội dung file service:

```ini
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/ba_datn
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname"
Environment="SPRING_DATASOURCE_USERNAME=postgres"
Environment="SPRING_DATASOURCE_PASSWORD=password"
Environment="JWT_SECRET=your-secret-key"
Environment="MAIL_USERNAME=your_email@gmail.com"
Environment="MAIL_PASSWORD=your_app_password"
ExecStart=/usr/bin/java -jar /home/ubuntu/ba_datn/target/mycode-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Enable và start service
sudo systemctl daemon-reload
sudo systemctl enable ba-datn
sudo systemctl start ba-datn

# Check status
sudo systemctl status ba-datn
```

### 4.6. Cấu Hình Nginx (Tùy chọn)

```bash
# Cài đặt Nginx
sudo apt install nginx -y

# Cấu hình reverse proxy
sudo nano /etc/nginx/sites-available/ba-datn
```

Nội dung:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/ba-datn /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

**Ưu điểm Oracle Cloud:**
- ✅ Free vĩnh viễn (2 VMs, mỗi VM 1GB RAM)
- ✅ Full control, có thể cài đặt bất cứ gì
- ✅ Không giới hạn bandwidth
- ✅ Phù hợp cho production

**Nhược điểm:**
- ⚠️ Cần cấu hình thủ công nhiều hơn
- ⚠️ Cần kiến thức về Linux/SSH

---

## 5. Deploy Lên Fly.io

Fly.io cung cấp 3 VMs miễn phí, hiệu năng tốt nhưng cần Dockerfile.

### 5.1. Cài Đặt Fly CLI

```bash
# Mac
curl -L https://fly.io/install.sh | sh

# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex

# Linux
curl -L https://fly.io/install.sh | sh
```

### 5.2. Đăng Nhập

```bash
fly auth login
```

### 5.3. Tạo Fly App

```bash
cd /path/to/your/project
fly launch
```

Fly sẽ hỏi:
- App name: `ba-datn-backend` (hoặc tên bạn muốn)
- Region: `sin` (Singapore) hoặc `nrt` (Tokyo)
- PostgreSQL: Chọn **"Yes"** để tạo database

### 5.4. Cấu Hình fly.toml

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

### 5.5. Cấu Hình Secrets (Environment Variables)

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

### 5.6. Cấu Hình Database Connection

Nếu đã tạo PostgreSQL:

```bash
# Xem database URL
fly postgres connect -a ba-datn-backend-db

# Set database URL
fly secrets set SPRING_DATASOURCE_URL="jdbc:postgresql://host:port/dbname"
fly secrets set SPRING_DATASOURCE_USERNAME="postgres"
fly secrets set SPRING_DATASOURCE_PASSWORD="password"
```

### 5.7. Deploy

```bash
fly deploy
```

### 5.8. Xem Logs

```bash
fly logs
```

### 5.9. Mở App

```bash
fly open
```

URL sẽ có dạng: `https://ba-datn-backend.fly.dev`

---

## 6. Deploy Lên Render.com ⚠️ Không Khuyến Nghị

Render có thể gặp vấn đề với Java runtime detection. Nếu muốn thử, xem hướng dẫn bên dưới.

### 6.1. Đăng Ký Tài Khoản

1. Truy cập: https://render.com
2. Đăng nhập bằng GitHub
3. Xác nhận email

### 6.2. Tạo PostgreSQL Database

1. Dashboard → **"New +"** → **"PostgreSQL"**
2. Đặt tên database
3. Chọn **"Free"** plan
4. Chọn region (Singapore gần VN nhất)
5. Click **"Create Database"**
6. Copy **Internal Database URL** và **External Database URL**

### 6.3. Tạo Web Service

1. Dashboard → **"New +"** → **"Web Service"**
2. Connect GitHub repository
3. Chọn repository và branch

### 6.4. Cấu Hình Build & Deploy

**Basic Settings:**
- **Name**: `ba-datn-backend` (hoặc tên bạn muốn)
- **Region**: Singapore
- **Branch**: `main`
- **Root Directory**: `/` (mặc định)
- **Runtime**: `Java` ⚠️ **QUAN TRỌNG**: Phải chọn **Java**, không phải Node.js!
- **Java Version**: `17` (hoặc để mặc định nếu có option)
- **Build Command**: 
  ```bash
  chmod +x mvnw && ./mvnw clean package -DskipTests
  ```
- **Start Command**: 
  ```bash
  java -jar target/mycode-0.0.1-SNAPSHOT.jar
  ```

**⚠️ Lưu ý quan trọng:**
- Nếu Render tự động detect **Node.js**, bạn phải **thay đổi Runtime thành Java** trong Settings
- Nếu gặp lỗi `JAVA_HOME is not defined`, thêm vào **Environment Variables**:
  ```bash
  JAVA_HOME=/usr/lib/jvm/java-17-openjdk
  ```
  Hoặc thử build command với JAVA_HOME:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk && chmod +x mvnw && ./mvnw clean package -DskipTests
  ```

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

## 5. Deploy Lên Fly.io

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

### 6.5. Cấu Hình Environment Variables

Trong tab **"Environment"**, thêm:

```bash
# Database (sử dụng Internal Database URL từ PostgreSQL service)
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname
SPRING_DATASOURCE_USERNAME=render
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

# Port (Render tự động set PORT)
PORT=8080

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### 6.6. Deploy

1. Click **"Create Web Service"**
2. Render sẽ tự động build và deploy
3. Xem logs trong tab **"Logs"**
4. URL: `https://your-app-name.onrender.com`

### 6.7. Lưu ý Free Tier

- Service sẽ **sleep sau 15 phút** không có request
- Request đầu tiên sau khi sleep sẽ mất **30-60 giây** để wake up
- Có thể dùng **UptimeRobot** (miễn phí) để ping mỗi 5 phút

---

## 7. Cấu Hình Database

### 7.1. Chuyển Từ SQL Server Sang PostgreSQL

Nếu bạn đang dùng SQL Server và muốn chuyển sang PostgreSQL (miễn phí trên Railway/Koyeb):

#### 7.1.1. Thêm Dependency PostgreSQL

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

## 8. Troubleshooting

### 6.1. Build Failed

**Lỗi**: `Maven not found` hoặc `Java not found` hoặc `Permission denied` hoặc `JAVA_HOME is not defined`

**Giải pháp**:

1. **JAVA_HOME is not defined** (Render):
   - **Kiểm tra Runtime**: Vào Settings → đảm bảo **Runtime = Java** (không phải Node.js)
   - **Thêm Environment Variable**:
     ```bash
     JAVA_HOME=/usr/lib/jvm/java-17-openjdk
     ```
   - **Hoặc cập nhật Build Command**:
     ```bash
     export JAVA_HOME=/usr/lib/jvm/java-17-openjdk && chmod +x mvnw && ./mvnw clean package -DskipTests
     ```
   - **Hoặc thử**:
     ```bash
     export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::") && chmod +x mvnw && ./mvnw clean package -DskipTests
     ```

2. **Permission denied với mvnw**: Thêm `chmod +x mvnw &&` vào đầu build command:
   ```bash
   chmod +x mvnw && ./mvnw clean package -DskipTests
   ```

3. **Runtime sai (Node.js thay vì Java)**:
   - Vào Settings → Runtime → Chọn **Java**
   - Render có thể tự động detect Node.js nếu có file `package.json`

4. Railway/Render: Đảm bảo có `mvnw` hoặc `mvnw.cmd` trong project

5. Fly.io: Kiểm tra Dockerfile có cài đặt Maven đúng chưa

6. **Git không giữ quyền thực thi**: Chạy lệnh sau và commit:
   ```bash
   git update-index --chmod=+x mvnw
   git commit -m "Add executable permission to mvnw"
   ```

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

## 9. So Sánh Các Nền Tảng

| Tính năng | Railway | Koyeb | Oracle Cloud | Fly.io | Render |
|-----------|---------|-------|--------------|--------|--------|
| **Free Tier** | $5 credit/tháng | Free (giới hạn) | 2 VMs vĩnh viễn | 3 VMs free | Free (có giới hạn) |
| **Database** | PostgreSQL miễn phí | PostgreSQL miễn phí | Tự cài đặt | PostgreSQL (trả phí) | PostgreSQL miễn phí |
| **Auto Deploy** | ✅ | ✅ | ❌ (thủ công) | ✅ | ✅ |
| **Sleep Mode** | ❌ | ❌ | ❌ | ❌ | ✅ (15 phút) |
| **Custom Domain** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Dockerfile** | Không cần | Không cần | Không cần | Cần | Không cần |
| **Java Support** | ⭐⭐⭐ Tuyệt vời | ⭐⭐⭐ Tuyệt vời | ⭐⭐⭐ Full control | ⭐⭐⭐ Tốt | ⭐ Có vấn đề |
| **Độ khó** | ⭐ Dễ | ⭐ Rất dễ | ⭐⭐⭐ Khó | ⭐⭐ Trung bình | ⭐⭐ Trung bình |
| **Tốc độ** | ⭐⭐⭐ Nhanh | ⭐⭐⭐ Nhanh | ⭐⭐⭐ Rất nhanh | ⭐⭐⭐ Rất nhanh | ⭐⭐ Trung bình |

**Khuyến nghị**:
- **Người mới, muốn dễ nhất**: **Koyeb** ⭐
- **Cần free tier ổn định**: **Railway** ⭐
- **Cần full control, free vĩnh viễn**: **Oracle Cloud** ⭐
- **Cần hiệu năng cao**: **Fly.io**
- **Không khuyến nghị**: **Render** (có vấn đề với Java)

---

## 10. Liên Kết Hữu Ích

- **Railway**: https://docs.railway.app
- **Koyeb**: https://www.koyeb.com/docs
- **Oracle Cloud**: https://docs.oracle.com/en-us/iaas/Content/home.htm
- **Fly.io**: https://fly.io/docs
- **Render**: https://render.com/docs (không khuyến nghị cho Java)
- Spring Boot Deployment: https://spring.io/guides/gs/spring-boot-for-azure/
- PostgreSQL Migration: https://www.postgresql.org/docs/current/migration.html

---

## 11. Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra logs chi tiết
2. Xem documentation của platform
3. Search trên Stack Overflow
4. Tạo issue trên GitHub repo

**Chúc bạn deploy thành công! 🎉**

