# Hướng Dẫn Kết Nối và Tạo Dữ Liệu trên PostgreSQL Render

## Thông Tin Database

- **Hostname**: `dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com`
- **Port**: `5432`
- **Database**: `ba_datn_db`
- **Username**: `ba_datn_db_user`
- **Password**: `lNxER7uBbNYw60imnHx6coVCKyiUvSDH`

## Cách 1: Kết Nối Qua Render Dashboard (Khuyến nghị)

### Bước 1: Vào Render Dashboard
1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)
2. Vào database service của bạn
3. Click tab **"Connect"**

### Bước 2: Copy Connection String
Render sẽ hiển thị các cách kết nối:
- **PSQL Command**: Copy command này
- **External Database URL**: Dùng để kết nối từ bên ngoài

### Bước 3: Chạy SQL Script
1. Vào tab **"Connect"** → **"Query Tool"** (nếu có)
2. Hoặc dùng PSQL command từ Render Dashboard

## Cách 2: Kết Nối Qua PSQL (Từ Terminal)

### Bước 1: Cài đặt PostgreSQL Client (nếu chưa có)

**macOS:**
```bash
brew install postgresql
```

**Linux:**
```bash
sudo apt-get install postgresql-client
```

**Windows:**
- Download từ: https://www.postgresql.org/download/windows/

### Bước 2: Kết Nối Database

```bash
PGPASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH psql -h dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com -U ba_datn_db_user -d ba_datn_db
```

### Bước 3: Chạy SQL Script

Sau khi kết nối thành công, bạn có thể:

1. **Chạy file SQL:**
```bash
PGPASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH psql -h dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com -U ba_datn_db_user -d ba_datn_db -f init-database-render.sql
```

2. **Hoặc chạy từng lệnh SQL:**
```sql
-- Xem tất cả tables
\dt

-- Xem dữ liệu trong một table
SELECT * FROM chuc_vu;

-- Chạy script tạo dữ liệu
\i init-database-render.sql
```

## Cách 3: Để Spring Boot Tự Động Tạo Dữ Liệu (Khuyến nghị nhất)

**Không cần chạy SQL thủ công!** 

Khi bạn deploy ứng dụng lên Render:
1. Hibernate sẽ tự động tạo schema (từ `ddl-auto=update`)
2. `DataInitializer` sẽ tự động tạo dữ liệu mẫu khi database trống
3. Tất cả dữ liệu sẽ được tạo tự động

### Các bước:
1. **Deploy ứng dụng lên Render** (đã push code lên GitHub)
2. **Set environment variables** trên Render Dashboard
3. **Deploy service** - Hibernate sẽ tạo schema
4. **DataInitializer tự động chạy** và tạo dữ liệu

## Kiểm Tra Dữ Liệu Đã Tạo

### Qua PSQL:
```sql
-- Xem tất cả chức vụ
SELECT * FROM chuc_vu;

-- Xem tất cả thương hiệu
SELECT * FROM thuong_hieu;

-- Xem tất cả màu sắc
SELECT * FROM mau_sac;

-- Xem tài khoản admin
SELECT id, ma, ho_ten, email, tai_khoan, trang_thai 
FROM nhan_vien 
WHERE tai_khoan = 'admin';
```

### Qua Render Dashboard:
1. Vào database service
2. Tab **"Connect"** → **"Query Tool"** (nếu có)
3. Chạy các query trên

## Lưu Ý Quan Trọng

1. **Password**: Lưu password cẩn thận, không commit vào git
2. **Connection**: Dùng **External Database URL** khi kết nối từ bên ngoài
3. **Security**: Render chỉ cho phép kết nối từ IP được whitelist (nếu có)
4. **DataInitializer**: Tự động chạy khi deploy, không cần chạy SQL thủ công

## Troubleshooting

### Lỗi: "Connection refused"
- Kiểm tra database đang chạy (status = "Available")
- Kiểm tra hostname có đúng không
- Kiểm tra password

### Lỗi: "Database does not exist"
- Kiểm tra tên database: `ba_datn_db`
- Vào Render Dashboard để xem tên database chính xác

### Lỗi: "Permission denied"
- Kiểm tra username: `ba_datn_db_user`
- Kiểm tra password có đúng không

## Tài Khoản Admin

Sau khi deploy, tài khoản admin sẽ được tạo tự động:
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@datn.com`

⚠️ **QUAN TRỌNG**: Đổi mật khẩu ngay sau khi đăng nhập!

