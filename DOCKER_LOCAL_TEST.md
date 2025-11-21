# 🐳 Hướng Dẫn Test API Local với Docker

## 📋 Yêu Cầu

- Docker Desktop đã cài đặt và đang chạy
- Docker Compose (thường đi kèm với Docker Desktop)
- Port 8080 và 1433 chưa được sử dụng

## 🚀 Các Bước Chạy

### 1. Build và Chạy Docker Compose

```bash
# Build và chạy tất cả services (SQL Server + Spring Boot App)
docker-compose -f docker-compose.local.yml up --build

# Hoặc chạy ở background
docker-compose -f docker-compose.local.yml up -d --build
```

### 2. Kiểm Tra Logs

```bash
# Xem logs của tất cả services
docker-compose -f docker-compose.local.yml logs -f

# Xem logs của app chỉ
docker-compose -f docker-compose.local.yml logs -f app

# Xem logs của database
docker-compose -f docker-compose.local.yml logs -f sqlserver
```

### 3. Kiểm Tra Health

```bash
# Kiểm tra app đã sẵn sàng chưa
curl http://localhost:8080/actuator/health

# Hoặc mở browser
# http://localhost:8080/actuator/health
```

### 4. Test API

#### Test Login Endpoint

```bash
# Test login nhân viên
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4200" \
  -d '{
    "username": "your_username",
    "pass": "your_password"
  }'
```

#### Test với Swagger UI

Mở browser và truy cập:
```
http://localhost:8080/swagger-ui.html
```

## 🛠️ Các Lệnh Hữu Ích

### Dừng Services

```bash
# Dừng nhưng giữ containers
docker-compose -f docker-compose.local.yml stop

# Dừng và xóa containers
docker-compose -f docker-compose.local.yml down

# Dừng và xóa cả volumes (⚠️ Xóa dữ liệu database)
docker-compose -f docker-compose.local.yml down -v
```

### Rebuild App (khi code thay đổi)

```bash
# Rebuild chỉ app service
docker-compose -f docker-compose.local.yml up -d --build app

# Hoặc rebuild tất cả
docker-compose -f docker-compose.local.yml up -d --build
```

### Xem Status

```bash
# Xem trạng thái các containers
docker-compose -f docker-compose.local.yml ps

# Xem resource usage
docker stats
```

### Vào Container

```bash
# Vào container của app
docker exec -it datn-app-local bash

# Vào container của database
docker exec -it datn-sqlserver-local bash

# Chạy SQL command
docker exec -it datn-sqlserver-local /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P 'Password.1' \
  -Q "SELECT name FROM sys.databases"
```

## 🔧 Cấu Hình

### Environment Variables

Bạn có thể override các biến môi trường bằng cách:

1. **Tạo file `.env`** trong thư mục root:
```env
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000
JWT_SECRET=your-secret-key-here
```

2. **Hoặc export trước khi chạy**:
```bash
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
docker-compose -f docker-compose.local.yml up
```

### Thay Đổi Ports

Nếu port 8080 hoặc 1433 đã được sử dụng, sửa trong `docker-compose.local.yml`:

```yaml
services:
  app:
    ports:
      - "8081:8080"  # Thay đổi port host
      
  sqlserver:
    ports:
      - "1434:1433"  # Thay đổi port host
```

Sau đó update `SPRING_DATASOURCE_URL` trong environment của app service.

## 🐛 Troubleshooting

### App không start được

1. **Kiểm tra logs**:
```bash
docker-compose -f docker-compose.local.yml logs app
```

2. **Kiểm tra database đã sẵn sàng chưa**:
```bash
docker-compose -f docker-compose.local.yml logs sqlserver
```

3. **Kiểm tra health check**:
```bash
docker-compose -f docker-compose.local.yml ps
```

### Database connection error

1. **Kiểm tra SQL Server đã chạy**:
```bash
docker exec -it datn-sqlserver-local /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P 'Password.1' -Q "SELECT 1"
```

2. **Kiểm tra network**:
```bash
docker network inspect datn-network
```

3. **Restart services**:
```bash
docker-compose -f docker-compose.local.yml restart
```

### Port đã được sử dụng

```bash
# Kiểm tra process đang dùng port
lsof -i :8080
lsof -i :1433

# Kill process hoặc đổi port trong docker-compose.local.yml
```

### Rebuild từ đầu

```bash
# Xóa tất cả và rebuild
docker-compose -f docker-compose.local.yml down -v
docker-compose -f docker-compose.local.yml build --no-cache
docker-compose -f docker-compose.local.yml up
```

## 📊 Kiểm Tra Database

### Kết nối từ Host Machine

Nếu bạn có SQL Server Management Studio hoặc Azure Data Studio:

- **Server**: `localhost,1433`
- **Username**: `sa`
- **Password**: `Password.1`
- **Database**: `DATN_SD40` (sẽ được tạo tự động khi app chạy lần đầu)

### Query từ Command Line

```bash
docker exec -it datn-sqlserver-local /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P 'Password.1' \
  -d DATN_SD40 \
  -Q "SELECT TOP 5 * FROM NhanVien"
```

## 🔗 Test với Frontend (Angular)

1. **Đảm bảo CORS đã được cấu hình** trong `application-docker.properties`:
```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:3000
```

2. **Chạy Angular dev server**:
```bash
cd your-angular-project
ng serve
```

3. **Test login từ Angular app**:
   - URL: `http://localhost:8080/api/auth/login/nhan-vien`
   - Method: POST
   - Headers: `Content-Type: application/json`
   - Body: `{ "username": "...", "pass": "..." }`

## ✅ Checklist Test

- [ ] SQL Server container đã chạy và healthy
- [ ] App container đã chạy và healthy
- [ ] Health endpoint trả về `{"status":"UP"}`
- [ ] Swagger UI accessible tại `http://localhost:8080/swagger-ui.html`
- [ ] Login endpoint trả về 200 với JWT token
- [ ] CORS headers có trong response
- [ ] Database connection thành công
- [ ] Logs không có lỗi

## 📝 Notes

- Database sẽ được tạo tự động khi app chạy lần đầu (do `ddl-auto=update`)
- Dữ liệu sẽ được lưu trong Docker volume `sqlserver_data`
- Để giữ dữ liệu khi restart, không dùng `docker-compose down -v`
- Để reset database, dùng `docker-compose down -v` và start lại

---

**Happy Testing! 🚀**

