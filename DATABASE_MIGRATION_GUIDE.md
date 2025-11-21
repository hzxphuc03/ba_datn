# Hướng Dẫn Migrate Database Từ Local Lên Hosting

## Tổng Quan

Dự án này đang sử dụng:
- **Local**: SQL Server (DATN_SD40)
- **Hosting**: PostgreSQL (theo cấu hình production)

## Các Cách Export Database

### Cách 1: Export bằng Docker + BCP (Khuyến nghị cho macOS - Không cần cài tools)

```bash
# Cấp quyền thực thi
chmod +x export-with-docker.sh

# Chạy export (sử dụng Docker container)
./export-with-docker.sh
```

**Ưu điểm**:
- Không cần cài đặt mssql-tools trên máy
- Sử dụng Docker container có sẵn
- Dễ import vào PostgreSQL
- Mỗi bảng được export thành file CSV riêng

### Cách 2: Export bằng BCP (Nếu đã cài mssql-tools)

```bash
# Cấp quyền thực thi
chmod +x export-with-bcp.sh

# Chạy export
./export-with-bcp.sh
```

**Kết quả**: Mỗi bảng được export thành file CSV riêng trong thư mục `database-export/bcp-export/`

**Ưu điểm**:
- Dễ import vào PostgreSQL
- Có thể xem và chỉnh sửa dữ liệu
- Hỗ trợ import từng bảng

### Cách 3: Export sang file SQL bằng Docker

```bash
chmod +x export-sql-with-docker.sh
./export-sql-with-docker.sh
```

Hoặc nếu đã cài mssql-tools:
```bash
chmod +x export-database-sql.sh
./export-database-sql.sh
```

**Kết quả**: File SQL chứa schema và dữ liệu

**Lưu ý**: File SQL từ SQL Server cần chỉnh sửa để tương thích với PostgreSQL

### Cách 4: Export backup file (.bak)

```bash
chmod +x export-database.sh
./export-database.sh
```

**Kết quả**: File .bak (chỉ dùng để restore vào SQL Server khác)

**Lưu ý**: Cần cài mssql-tools hoặc dùng Docker

## Import Vào PostgreSQL (Hosting)

### Bước 1: Export dữ liệu từ local

**Trên macOS (khuyến nghị dùng Docker):**
```bash
./export-with-docker.sh
```

**Hoặc nếu đã cài mssql-tools:**
```bash
./export-with-bcp.sh
```

### Bước 2: Cấu hình thông tin PostgreSQL

Sửa file `import-to-postgresql.sh` hoặc set environment variables:

```bash
export PG_HOST="dpg-d4ftetq4d50c73f1mit0-a.oregon-postgres.render.com"
export PG_PORT="5432"
export PG_DATABASE="ba_datn_db_demo"
export PG_USER="ba_datn_db_demo_user"
export PG_PASSWORD="DooMV6sjYKxbLPCYhfgvt8SOmynXcnCs"
```

### Bước 3: Import dữ liệu

```bash
chmod +x import-to-postgresql.sh
./import-to-postgresql.sh
```

## Import Thủ Công Bằng pgAdmin hoặc psql

### Sử dụng psql:

```bash
# Kết nối đến database
psql -h dpg-d4ftetq4d50c73f1mit0-a.oregon-postgres.render.com \
     -p 5432 \
     -U ba_datn_db_demo_user \
     -d ba_datn_db_demo

# Import từng bảng
\copy table_name FROM '/path/to/file.csv' WITH (FORMAT csv, DELIMITER ',', HEADER false);
```

### Sử dụng pgAdmin:

1. Mở pgAdmin và kết nối đến database
2. Right-click vào bảng cần import → Import/Export Data
3. Chọn file CSV
4. Cấu hình:
   - Format: CSV
   - Delimiter: `,`
   - Header: No
   - Encoding: UTF8

## Lưu Ý Quan Trọng

### 1. Schema đã được tạo tự động

Với cấu hình `spring.jpa.hibernate.ddl-auto=update`, Hibernate sẽ tự động tạo schema khi app khởi động. Bạn chỉ cần import dữ liệu.

### 2. Chuyển đổi kiểu dữ liệu

Một số kiểu dữ liệu cần chuyển đổi:

| SQL Server | PostgreSQL |
|------------|------------|
| `NVARCHAR` | `VARCHAR` |
| `DATETIME` | `TIMESTAMP` |
| `BIGINT IDENTITY` | `BIGSERIAL` |
| `INT` | `INTEGER` |

### 3. Thứ tự import

Nếu có foreign keys, import theo thứ tự:
1. Bảng không có foreign key
2. Bảng có foreign key (theo thứ tự dependency)

### 4. Kiểm tra dữ liệu sau import

```sql
-- Đếm số bản ghi
SELECT 
    schemaname,
    tablename,
    n_live_tup as row_count
FROM pg_stat_user_tables
ORDER BY tablename;
```

## Troubleshooting

### Lỗi: "relation does not exist"

**Nguyên nhân**: Bảng chưa được tạo trong PostgreSQL

**Giải pháp**: 
- Đảm bảo app đã chạy ít nhất 1 lần để Hibernate tạo schema
- Hoặc tạo schema thủ công từ file `DATN_V2.sql` (cần chỉnh sửa cho PostgreSQL)

### Lỗi: "permission denied"

**Nguyên nhân**: User không có quyền import

**Giải pháp**: Kiểm tra quyền của user trong PostgreSQL

### Lỗi: "encoding mismatch"

**Nguyên nhân**: File CSV không đúng encoding

**Giải pháp**: 
```bash
# Convert sang UTF-8
iconv -f WINDOWS-1252 -t UTF-8 input.csv > output.csv
```

## Cài Đặt Tools Cần Thiết

### macOS:

**Cách 1: Sử dụng Docker (Khuyến nghị - Không cần cài thêm tools)**

Dự án đã có Docker setup, chỉ cần:
```bash
# Đảm bảo Docker Desktop đang chạy
# Không cần cài thêm gì, script sẽ dùng Docker container
```

**Cách 2: Cài đặt trực tiếp (Nếu không dùng Docker)**

```bash
# PostgreSQL client
brew install postgresql

# SQL Server tools (không có sẵn trên Homebrew)
# Cần tải từ Microsoft hoặc dùng Docker
# Hoặc cài Azure Data Studio: https://aka.ms/azuredatastudio
```

### Linux:

```bash
# SQL Server tools
curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
curl https://packages.microsoft.com/config/ubuntu/20.04/prod.list | sudo tee /etc/apt/sources.list.d/mssql-release.list
sudo apt-get update
sudo ACCEPT_EULA=Y apt-get install -y mssql-tools

# PostgreSQL client
sudo apt-get install postgresql-client
```

## Tự Động Hóa Với Script

Tạo file `migrate-all.sh` để tự động hóa toàn bộ quá trình:

```bash
#!/bin/bash
echo "1. Exporting from SQL Server..."
./export-with-bcp.sh

echo "2. Importing to PostgreSQL..."
./import-to-postgresql.sh

echo "3. Done!"
```

## Backup Trước Khi Import

**QUAN TRỌNG**: Luôn backup database trên hosting trước khi import!

```bash
# Backup PostgreSQL
pg_dump -h $PG_HOST -p $PG_PORT -U $PG_USER -d $PG_DATABASE > backup_$(date +%Y%m%d).sql
```

## Liên Hệ

Nếu gặp vấn đề, kiểm tra:
1. Logs của application
2. Logs của PostgreSQL
3. File CSV có đúng format không
4. Encoding của file CSV

