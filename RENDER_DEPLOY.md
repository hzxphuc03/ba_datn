# Deploying Spring Boot Application on Render

Hướng dẫn deploy ứng dụng Spring Boot lên Render theo [Medium article](https://medium.com/@pmanaktala/deploying-a-spring-boot-application-on-render-4e757dfe92ed).

## 📋 Yêu Cầu

- Tài khoản Render (https://render.com)
- GitHub repository chứa code
- Render PostgreSQL Database (miễn phí - sẽ tạo trong hướng dẫn)

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

### Bước 2: Tạo PostgreSQL Database trên Render (MIỄN PHÍ)

1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)

2. Click **New +** → **PostgreSQL**

3. **Cấu hình Database**:
   - **Name**: `datn-database` (hoặc tên bạn muốn)
   - **Database**: `datn_db` (hoặc để mặc định)
   - **Region**: Chọn region gần nhất (ví dụ: Singapore)
   - **PostgreSQL Version**: Để mặc định
   - **Plan**: **Free** (100 MB storage - đủ cho development)

4. Click **Create Database**

5. **Lưu lại thông tin**: Sau khi tạo xong, Render sẽ hiển thị thông tin kết nối. Bạn sẽ cần:
   - **Internal Database URL** (dạng: `postgresql://user:password@host:port/database`)
   - Hoặc các thông tin riêng lẻ: Host, Port, Database, User, Password

### Bước 3: Tạo Web Service trên Render

1. Trong Render Dashboard, click **New +** → **Web Service**

2. **Connect a repository**:
   - Chọn GitHub account
   - Chọn repository chứa code
   - Chọn branch (thường là `main` hoặc `master`)

3. **Cấu hình Service**:
   - **Name**: `datn-backend` (hoặc tên bạn muốn)
   - **Region**: Chọn cùng region với database (ví dụ: Singapore)
   - **Branch**: `main` (hoặc branch bạn muốn deploy)
   - **Root Directory**: Để trống (hoặc `./`)
   - **Runtime**: **Docker** ⚠️ (quan trọng!)
   - **Dockerfile Path**: `Dockerfile` (hoặc để mặc định)
   - **Docker Context**: `.` (root directory)

4. **Instance Type**:
   - **Free**: 512 MB RAM (có thể không đủ cho Spring Boot)
   - **Starter**: $7/tháng - 512 MB RAM
   - **Standard**: $25/tháng - 2 GB RAM (khuyến nghị)

5. **Link Database** (QUAN TRỌNG - Đơn giản nhất):

   **Cách link database:**
   - Scroll xuống phần **Environment** (hoặc **Environment Variables**)
   - Tìm phần **Add Environment Variable**
   - Click nút **Link Database** hoặc **Link Existing Database**
   - Một popup sẽ hiện ra với danh sách databases
   - Chọn database `ba_datn_db_demo` (hoặc tên database bạn đã tạo)
   - Click **Link** hoặc **Connect**
   - Render sẽ tự động thêm biến `DATABASE_URL` với format: `postgresql://user:password@host:port/database`
   
   **Sau khi link xong**, bạn sẽ thấy biến `DATABASE_URL` đã được thêm tự động trong danh sách Environment Variables.

6. **Auto-Deploy**: Bật để tự động deploy khi push code

### Bước 4: Cấu Hình Environment Variables

Trong phần **Environment Variables**, thêm các biến sau:

#### Database Configuration (Render PostgreSQL)

**Thông tin database của bạn:**
- Host: `dpg-d4ftetq4d50c73f1mit0-a` (Internal) hoặc `dpg-d4ftetq4d50c73f1mit0-a.oregon-postgres.render.com` (External)
- Port: `5432`
- Database: `ba_datn_db_demo`
- Username: `ba_datn_db_demo_user`
- Password: `DooMV6sjYKxbLPCYhfgvt8SOmynXcnCs`

**Cách 1: Dùng Internal Database URL (Khuyến nghị - nếu app và database cùng region)**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4ftetq4d50c73f1mit0-a:5432/ba_datn_db_demo
SPRING_DATASOURCE_USERNAME=ba_datn_db_demo_user
SPRING_DATASOURCE_PASSWORD=DooMV6sjYKxbLPCYhfgvt8SOmynXcnCs
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

**Cách 2: Dùng External Database URL (Nếu app và database khác region)**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4ftetq4d50c73f1mit0-a.oregon-postgres.render.com:5432/ba_datn_db_demo
SPRING_DATASOURCE_USERNAME=ba_datn_db_demo_user
SPRING_DATASOURCE_PASSWORD=DooMV6sjYKxbLPCYhfgvt8SOmynXcnCs
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

**Cách 3: Nếu đã link database (Render tự động thêm DATABASE_URL)**
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

**Lưu ý**: 
- Nếu app và database cùng region (Oregon) → Dùng **Cách 1** (Internal URL - nhanh hơn)
- Nếu app và database khác region → Dùng **Cách 2** (External URL)
- Nếu đã link database → Dùng **Cách 3** (đơn giản nhất)

#### JWT Configuration (Bắt buộc)
```
JWT_SECRET=your-secret-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

#### Email Configuration (Tùy chọn)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

#### CORS Configuration (Tùy chọn)
```
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

**Lưu ý quan trọng**:
- ❌ **KHÔNG** set biến `PORT` - Render sẽ tự động inject
- ✅ Database sẽ tự động tạo tables khi app start (nhờ `spring.jpa.hibernate.ddl-auto=update`)
- ✅ `JWT_SECRET` phải dài ít nhất 32 ký tự (ví dụ: `my-super-secret-key-for-jwt-token-generation-2024`)

### Bước 5: Deploy

1. Click **Create Web Service**
2. Render sẽ bắt đầu build Docker image
3. Quá trình build có thể mất 5-10 phút
4. Theo dõi **Logs** tab để xem tiến trình

### Bước 6: Kiểm Tra

1. Sau khi deploy thành công, bạn sẽ có URL: `https://your-app.onrender.com`
2. Test API endpoints
3. Kiểm tra logs nếu có lỗi
4. Database tables sẽ tự động được tạo khi app start lần đầu

## 🔧 Troubleshooting

### Build Failed
- Kiểm tra logs để xem lỗi cụ thể
- Đảm bảo `Dockerfile` đúng format
- Kiểm tra `mvnw` và `.mvn/` có trong repository

### Cannot Connect to Database
- Kiểm tra đã link database với service chưa (Bước 3.5)
- Kiểm tra `SPRING_DATASOURCE_URL` có đúng format không
- Kiểm tra database đã được tạo và đang running
- Xem logs để biết lỗi cụ thể

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
- Database: Dùng Render PostgreSQL (miễn phí, 100 MB) - đã tạo ở Bước 2
- Database tables sẽ tự động tạo khi app start lần đầu
- Có thể setup custom domain trong Settings
- Nếu cần migrate dữ liệu từ SQL Server sang PostgreSQL, cần export/import data

## 🔗 Tài Liệu Tham Khảo

- [Render Documentation](https://render.com/docs)
- [Original Medium Article](https://medium.com/@pmanaktala/deploying-a-spring-boot-application-on-render-4e757dfe92ed)

