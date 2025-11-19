# 🔧 Fix Lỗi: "type nvarchar does not exist" trên PostgreSQL

## Vấn Đề

Khi deploy lên PostgreSQL, bạn gặp lỗi:
```
ERROR: type "nvarchar" does not exist
```

**Nguyên nhân**: 
- `NVARCHAR` là kiểu dữ liệu của SQL Server
- PostgreSQL không có kiểu `NVARCHAR`, chỉ có `VARCHAR` hoặc `TEXT`
- Hibernate đang cố tạo schema với `NVARCHAR` thay vì `VARCHAR`

## Giải Pháp

### Giải Pháp 1: Xóa Database và Tạo Lại (Khuyến nghị cho database mới)

Nếu database còn trống hoặc chưa có dữ liệu quan trọng:

1. **Vào Render Dashboard** → Database service
2. **Xóa database hiện tại**:
   - Vào tab **"Settings"**
   - Scroll xuống cuối
   - Click **"Delete Database"** hoặc **"Destroy"**
   - Xác nhận xóa

3. **Tạo database mới** (xem [HUONG_DAN_TAO_DATABASE_RENDER.md](./HUONG_DAN_TAO_DATABASE_RENDER.md))

4. **Deploy lại Web Service**:
   - Hibernate sẽ tự động tạo schema mới với PostgreSQL types (`VARCHAR` thay vì `NVARCHAR`)

### Giải Pháp 2: Đảm Bảo Cấu Hình Đúng

Đảm bảo các cấu hình sau trong `application-prod.properties`:

```properties
# Database driver phải là PostgreSQL
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate dialect phải là PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# ddl-auto phải là update để Hibernate tự tạo schema
spring.jpa.hibernate.ddl-auto=update
```

### Giải Pháp 3: Kiểm Tra Environment Variables trên Render

Đảm bảo các biến sau đã được set trên Render Dashboard:

```
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_PROFILES_ACTIVE=prod
```

### Giải Pháp 4: Xóa Tables và Để Hibernate Tạo Lại

Nếu database đã có tables với `NVARCHAR`:

1. **Kết nối database bằng PSQL**:
   ```bash
   PGPASSWORD=password psql -h hostname.region-postgres.render.com -U username database_name
   ```

2. **Xóa tất cả tables**:
   ```sql
   -- Xóa tất cả tables (cẩn thận, sẽ mất dữ liệu!)
   DROP SCHEMA public CASCADE;
   CREATE SCHEMA public;
   GRANT ALL ON SCHEMA public TO postgres;
   GRANT ALL ON SCHEMA public TO public;
   ```

3. **Restart Web Service**:
   - Hibernate sẽ tự động tạo lại schema với PostgreSQL types

### Giải Pháp 5: Sửa Thủ Công (Nếu có dữ liệu quan trọng)

Nếu database đã có dữ liệu và không thể xóa:

1. **Export dữ liệu**:
   ```bash
   pg_dump -h hostname -U username -d database_name > backup.sql
   ```

2. **Xóa và tạo lại schema** (xem Giải Pháp 4)

3. **Import lại dữ liệu** (sau khi Hibernate tạo schema mới):
   ```bash
   psql -h hostname -U username -d database_name < backup.sql
   ```

## Kiểm Tra Sau Khi Fix

1. **Xem logs** trên Render Dashboard
2. **Kiểm tra schema**:
   ```sql
   -- Kết nối database
   psql -h hostname -U username -d database_name
   
   -- Xem columns của một table
   \d table_name
   
   -- Kiểm tra xem có cột nào dùng NVARCHAR không
   SELECT column_name, data_type 
   FROM information_schema.columns 
   WHERE table_name = 'table_name';
   ```

3. **Đảm bảo tất cả columns dùng VARCHAR hoặc TEXT**, không có NVARCHAR

## Nguyên Nhân Chi Tiết

### Tại Sao Lại Có Lỗi Này?

1. **Database đã có schema từ SQL Server**:
   - Nếu bạn đã import schema từ SQL Server
   - Hoặc database đã được tạo với SQL Server types

2. **Hibernate detect sai database type**:
   - Có thể do connection string không đúng
   - Hoặc dialect không được set đúng

3. **Entities có columnDefinition với NVARCHAR**:
   - Kiểm tra xem có entity nào dùng `@Column(columnDefinition = "nvarchar")` không
   - Nếu có, cần sửa thành `VARCHAR` hoặc xóa columnDefinition

## Phòng Ngừa

Để tránh lỗi này trong tương lai:

1. ✅ **Luôn set dialect PostgreSQL** trong `application-prod.properties`
2. ✅ **Không dùng columnDefinition với SQL Server types** trong entities
3. ✅ **Xóa database và tạo lại** nếu migrate từ SQL Server sang PostgreSQL
4. ✅ **Kiểm tra schema** sau khi deploy để đảm bảo dùng đúng types

## Checklist Fix

- [ ] Đã kiểm tra `application-prod.properties` có dialect PostgreSQL
- [ ] Đã kiểm tra environment variables trên Render
- [ ] Đã xóa database cũ (nếu có)
- [ ] Đã tạo database mới
- [ ] Đã deploy lại Web Service
- [ ] Đã kiểm tra logs không còn lỗi NVARCHAR
- [ ] Đã kiểm tra schema trong database (tất cả columns dùng VARCHAR/TEXT)

## Tài Liệu Tham Khảo

- [PostgreSQL Data Types](https://www.postgresql.org/docs/current/datatype.html)
- [Hibernate Dialects](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#portability-dialect)
- [HUONG_DAN_TAO_DATABASE_RENDER.md](./HUONG_DAN_TAO_DATABASE_RENDER.md) - Hướng dẫn tạo database mới

---

**Sau khi fix, ứng dụng sẽ chạy thành công với PostgreSQL! 🎉**

