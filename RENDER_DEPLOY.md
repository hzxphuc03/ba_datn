# Deploying Spring Boot Application on Render

Hướng dẫn deploy ứng dụng Spring Boot lên Render theo [Medium article](https://medium.com/@pmanaktala/deploying-a-spring-boot-application-on-render-4e757dfe92ed).

## 📋 Yêu Cầu

- Tài khoản Render (https://render.com)
- GitHub repository chứa code
- Azure SQL Database (hoặc database khác)

## 🚀 Các Bước Deploy

### Bước 1: Chuẩn Bị Code

1. Đảm bảo có các file sau trong project:
   - `Dockerfile` (đã có sẵn)
   - `pom.xml`
   - `mvnw` và `.mvn/` directory
   - `src/` directory

2. Push code lên GitHub:
   ```bash
   git add .
   git commit -m "Prepare for Render deployment"
   git push origin main
   ```

### Bước 2: Tạo Web Service trên Render

1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)

2. Click **New +** → **Web Service**

3. **Connect a repository**:
   - Chọn GitHub account
   - Chọn repository chứa code
   - Chọn branch (thường là `main` hoặc `master`)

4. **Cấu hình Service**:
   - **Name**: `datn-backend` (hoặc tên bạn muốn)
   - **Region**: Chọn region gần nhất (ví dụ: Singapore)
   - **Branch**: `main` (hoặc branch bạn muốn deploy)
   - **Root Directory**: Để trống (hoặc `./`)
   - **Runtime**: **Docker** ⚠️ (quan trọng!)
   - **Dockerfile Path**: `Dockerfile` (hoặc để mặc định)
   - **Docker Context**: `.` (root directory)

5. **Instance Type**:
   - **Free**: 512 MB RAM (có thể không đủ cho Spring Boot)
   - **Starter**: $7/tháng - 512 MB RAM
   - **Standard**: $25/tháng - 2 GB RAM (khuyến nghị)

6. **Auto-Deploy**: Bật để tự động deploy khi push code

### Bước 3: Cấu Hình Environment Variables

Trong phần **Environment Variables**, thêm các biến sau:

#### Database Configuration (Azure SQL)
```
SPRING_DATASOURCE_URL=jdbc:sqlserver://your-server.database.windows.net:1433;databaseName=DATN_SD40;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
SPRING_DATASOURCE_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
SPRING_PROFILES_ACTIVE=prod
```

#### JWT Configuration
```
JWT_SECRET=your-secret-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

#### Email Configuration
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

#### CORS Configuration
```
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

**Lưu ý quan trọng**:
- ❌ **KHÔNG** set biến `PORT` - Render sẽ tự động inject
- ✅ Connection string cho Azure SQL phải có `encrypt=true` và `trustServerCertificate=false`
- ✅ `JWT_SECRET` phải dài ít nhất 32 ký tự

### Bước 4: Deploy

1. Click **Create Web Service**
2. Render sẽ bắt đầu build Docker image
3. Quá trình build có thể mất 5-10 phút
4. Theo dõi **Logs** tab để xem tiến trình

### Bước 5: Kiểm Tra

1. Sau khi deploy thành công, bạn sẽ có URL: `https://your-app.onrender.com`
2. Test API endpoints
3. Kiểm tra logs nếu có lỗi

## 🔧 Troubleshooting

### Build Failed
- Kiểm tra logs để xem lỗi cụ thể
- Đảm bảo `Dockerfile` đúng format
- Kiểm tra `mvnw` và `.mvn/` có trong repository

### Cannot Connect to Database
- Kiểm tra Azure SQL Firewall rules
- Kiểm tra connection string format
- Kiểm tra credentials

### Out of Memory
- Upgrade instance type
- Thêm `JAVA_OPTS=-Xmx512m -Xms256m` vào environment variables

### Application Won't Start
- Kiểm tra logs chi tiết
- Kiểm tra environment variables
- Đảm bảo database đã được tạo và accessible

## 📝 Notes

- Render tự động cung cấp HTTPS
- Free tier có thể sleep sau 15 phút không hoạt động
- Database nên dùng Azure SQL hoặc Render PostgreSQL (miễn phí)
- Có thể setup custom domain trong Settings

## 🔗 Tài Liệu Tham Khảo

- [Render Documentation](https://render.com/docs)
- [Original Medium Article](https://medium.com/@pmanaktala/deploying-a-spring-boot-application-on-render-4e757dfe92ed)

