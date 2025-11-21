# 🗄️ Hướng Dẫn Chi Tiết Tạo PostgreSQL Database Trên Render

Hướng dẫn từng bước một để tạo PostgreSQL database trên Render.com cho ứng dụng Spring Boot.

---

## 📋 Mục Lục

1. [Giới Thiệu](#1-giới-thiệu)
2. [Chuẩn Bị](#2-chuẩn-bị)
3. [Tạo Database (Từng Bước)](#3-tạo-database-từng-bước)
4. [Lưu Thông Tin Kết Nối](#4-lưu-thông-tin-kết-nối)
5. [Kiểm Tra Database](#5-kiểm-tra-database)
6. [Kết Nối Từ Terminal (Tùy chọn)](#6-kết-nối-từ-terminal-tùy-chọn)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. Giới Thiệu

### PostgreSQL trên Render

Render cung cấp PostgreSQL database với:
- ✅ **Free tier**: 90MB storage, đủ cho development và testing
- ✅ **Auto-backup**: Tự động backup hàng ngày (paid plan)
- ✅ **Connection pooling**: Hỗ trợ connection pooling
- ✅ **Internal & External URLs**: Kết nối từ trong và ngoài Render network

### Khi Nào Cần Tạo Database?

Bạn cần tạo database khi:
- ✅ Deploy ứng dụng Spring Boot lên Render lần đầu
- ✅ Cần database mới cho môi trường production
- ✅ Cần database riêng cho testing/staging

---

## 2. Chuẩn Bị

### Yêu Cầu

- [x] Đã có tài khoản Render (đăng ký tại https://render.com)
- [x] Đã đăng nhập vào Render Dashboard
- [x] Đã xác định region muốn tạo database (Singapore hoặc Oregon)

### Thông Tin Cần Chuẩn Bị

Trước khi tạo database, bạn cần quyết định:

1. **Tên Database**: Ví dụ: `ba-datn-db` hoặc `ba_datn_db`
2. **Region**: 
   - **Singapore**: Gần Việt Nam nhất, latency thấp
   - **Oregon (US West)**: Ổn định, nhiều features hơn
3. **Plan**: 
   - **Free**: 90MB, đủ cho development
   - **Starter ($7/tháng)**: 1GB storage
   - **Standard ($20/tháng)**: 10GB storage

---

## 3. Tạo Database (Từng Bước)

### Bước 1: Truy Cập Render Dashboard

1. Mở trình duyệt và truy cập: https://dashboard.render.com
2. Đăng nhập nếu chưa đăng nhập
3. Bạn sẽ thấy Render Dashboard với danh sách services

### Bước 2: Click "New +"

1. Ở góc trên bên phải màn hình, tìm nút **"New +"** (màu xanh)
2. Click vào nút này
3. Một dropdown menu sẽ xuất hiện với các options:
   - Web Service
   - **PostgreSQL** ← Chọn cái này
   - Redis
   - Static Site
   - Background Worker
   - Cron Job

### Bước 3: Chọn PostgreSQL

1. Click **"PostgreSQL"** từ dropdown menu
2. Bạn sẽ được chuyển đến trang cấu hình database

### Bước 4: Cấu Hình Database

Bạn sẽ thấy form cấu hình với các trường sau:

#### **4.1. Name (Tên Database Service)**

**Vị trí**: Trường đầu tiên trong form

**Mô tả**: 
- Tên này sẽ hiển thị trên Render Dashboard
- Dùng để phân biệt các database services

**Ví dụ**: 
```
ba-datn-db
```

**Lưu ý**:
- Chỉ dùng chữ thường, số, và dấu gạch ngang (`-`)
- Không được có khoảng trắng
- Không được trùng với service khác

**Gợi ý đặt tên**:
- `ba-datn-db` - Database cho project ba_datn
- `ba-datn-db-prod` - Database production
- `ba-datn-db-dev` - Database development

#### **4.2. Database (Tên Database Thực Tế)**

**Vị trí**: Trường thứ hai trong form

**Mô tả**:
- Tên database bên trong PostgreSQL
- Đây là tên database thực tế, không phải tên service

**Mặc định**: 
- Render tự động điền giống với Name
- Có thể để mặc định

**Ví dụ**:
```
ba_datn_db
```

**Lưu ý**:
- PostgreSQL cho phép dấu gạch dưới (`_`) trong tên database
- Tên này sẽ dùng trong connection string

#### **4.3. User (Username)**

**Vị trí**: Trường thứ ba trong form

**Mô tả**:
- Username để kết nối database
- Dùng để authenticate khi connect

**Mặc định**: 
- Render tự động điền `render`
- Có thể để mặc định hoặc đổi thành tên khác

**Ví dụ**:
```
render
hoặc
ba_datn_db_user
```

**Lưu ý**:
- Username này sẽ dùng trong connection string
- Nên dùng tên dễ nhớ và mô tả rõ mục đích

#### **4.4. Region (Khu Vực)**

**Vị trí**: Dropdown menu

**Mô tả**:
- Chọn region (khu vực) để host database
- Ảnh hưởng đến latency và features

**Các Options**:
- **Singapore**: Gần Việt Nam nhất, latency thấp (~50-100ms)
- **Oregon (US West)**: Ổn định, nhiều features, latency cao hơn (~200-300ms)
- **Frankfurt (EU)**: Nếu có users ở châu Âu
- **Ohio (US East)**: Nếu có users ở Mỹ

**Khuyến nghị**:
- Nếu users chủ yếu ở Việt Nam: Chọn **Singapore**
- Nếu cần features mới nhất: Chọn **Oregon**

**Lưu ý**:
- Database và Web Service nên ở cùng region để giảm latency
- Có thể chọn region khác nhau, nhưng sẽ chậm hơn

#### **4.5. PostgreSQL Version**

**Vị trí**: Dropdown menu

**Mô tả**:
- Chọn version PostgreSQL

**Các Options**:
- **16** (Mới nhất, khuyến nghị)
- **15** (Ổn định)
- **14** (Cũ hơn, ít dùng)

**Khuyến nghị**: 
- Chọn **16** hoặc **15** (mới nhất có sẵn)
- Version mới có nhiều features và performance tốt hơn

#### **4.6. Plan (Gói Dịch Vụ)**

**Vị trí**: Radio buttons hoặc dropdown

**Mô tả**:
- Chọn gói dịch vụ (free hoặc paid)

**Các Options**:

**Free**:
- ✅ 90MB storage
- ✅ Đủ cho development và testing
- ✅ Không giới hạn connections
- ❌ Không có auto-backup
- ❌ Không có point-in-time recovery

**Starter ($7/tháng)**:
- ✅ 1GB storage
- ✅ Auto-backup hàng ngày
- ✅ Point-in-time recovery
- ✅ Priority support

**Standard ($20/tháng)**:
- ✅ 10GB storage
- ✅ Auto-backup hàng ngày
- ✅ Point-in-time recovery
- ✅ High availability

**Khuyến nghị**:
- Bắt đầu với **Free** để test
- Upgrade lên **Starter** khi cần backup hoặc storage lớn hơn

### Bước 5: Xem Review và Tạo Database

1. Sau khi điền tất cả thông tin, scroll xuống cuối form
2. Bạn sẽ thấy phần **"Review"** hoặc **"Summary"** hiển thị:
   - Name
   - Database
   - User
   - Region
   - PostgreSQL Version
   - Plan
   - Estimated cost (nếu paid plan)

3. Kiểm tra lại tất cả thông tin:
   - ✅ Name đúng chưa?
   - ✅ Region đúng chưa?
   - ✅ Plan đúng chưa?

4. Click nút **"Create Database"** (màu xanh, ở cuối form)

### Bước 6: Chờ Database Được Tạo

1. Sau khi click "Create Database", bạn sẽ thấy:
   - Status: **"Creating..."**
   - Progress bar hoặc spinner
   - Thông báo: "Creating your database..."

2. **Thời gian tạo**: Thường mất **1-2 phút**

3. Bạn sẽ thấy logs:
   ```
   Creating database...
   Setting up PostgreSQL...
   Configuring network...
   Database created successfully!
   ```

4. Khi hoàn tất, status sẽ đổi thành **"Available"** (màu xanh)

5. Database sẽ xuất hiện trên Dashboard với:
   - Tên database
   - Status: **Available**
   - Region
   - Plan

---

## 4. Lưu Thông Tin Kết Nối

**⚠️ QUAN TRỌNG**: Lưu lại tất cả thông tin này ngay sau khi tạo database!

### Bước 1: Mở Database Service

1. Click vào database vừa tạo trên Dashboard
2. Bạn sẽ thấy trang chi tiết của database

### Bước 2: Vào Tab "Connections" hoặc "Info"

1. Tìm và click tab **"Connections"** hoặc **"Info"**
2. Bạn sẽ thấy tất cả thông tin kết nối

### Bước 3: Lưu Các Thông Tin Sau

#### **4.3.1. Hostname**

**Vị trí**: Trong phần "Connection Info"

**Format**:
```
dpg-xxxxx-a.region-postgres.render.com
```

**Ví dụ**:
```
dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com
```

**Lưu ý**:
- Hostname này dùng cho External connection
- Internal hostname sẽ khác (không có domain)

#### **4.3.2. Port**

**Mặc định**: `5432`

**Lưu ý**: Port này không đổi, luôn là 5432 cho PostgreSQL

#### **4.3.3. Database Name**

**Vị trí**: Trong phần "Connection Info"

**Ví dụ**:
```
ba_datn_db
```

**Lưu ý**: Đây là tên database thực tế, không phải tên service

#### **4.3.4. Username**

**Vị trí**: Trong phần "Connection Info"

**Ví dụ**:
```
ba_datn_db_user
hoặc
render
```

#### **4.3.5. Password**

**Vị trí**: Trong phần "Connection Info"

**⚠️ QUAN TRỌNG**: 
- Password chỉ hiển thị **một lần** khi tạo database
- Click nút **"Show"** để xem password
- **COPY và LƯU NGAY** password này!

**Ví dụ**:
```
lNxER7uBbNYw60imnHx6coVCKyiUvSDH
```

**Lưu ý**:
- Password dài và phức tạp
- Nếu quên password, cần reset (sẽ mất dữ liệu nếu không có backup)

#### **4.3.6. Internal Database URL**

**Vị trí**: Trong phần "Connection URLs"

**Format**:
```
postgresql://username:password@hostname:5432/database_name
```

**Ví dụ**:
```
postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a:5432/ba_datn_db
```

**Khi nào dùng**:
- Khi Web Service và Database ở **cùng Render network**
- Kết nối nhanh hơn, không qua internet

**Lưu ý**:
- Không có domain `.region-postgres.render.com`
- Chỉ dùng hostname ngắn: `dpg-xxxxx-a`

#### **4.3.7. External Database URL**

**Vị trí**: Trong phần "Connection URLs"

**Format**:
```
postgresql://username:password@hostname.region-postgres.render.com:5432/database_name
```

**Ví dụ**:
```
postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db
```

**Khi nào dùng**:
- Khi kết nối từ **bên ngoài Render** (local, other services)
- Khi Web Service và Database ở **khác region** hoặc **khác network**

**Lưu ý**:
- Có domain đầy đủ: `.oregon-postgres.render.com`
- Dùng cho JDBC connection string

#### **4.3.8. Connection Pooling URL**

**Vị trí**: Trong phần "Connection URLs" (nếu có)

**Format**: Tương tự External URL nhưng có thêm connection pooling

**Khi nào dùng**:
- **Khuyến nghị** cho production
- Tối ưu performance với nhiều connections

**Lưu ý**:
- Chỉ có ở paid plans
- Free tier không có connection pooling URL

### Bước 4: Tạo File Lưu Thông Tin

**Tạo file text để lưu thông tin** (KHÔNG commit vào Git!):

```
=== DATABASE CONNECTION INFO ===
Database Name: ba_datn_db
Hostname: dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com
Port: 5432
Username: ba_datn_db_user
Password: lNxER7uBbNYw60imnHx6coVCKyiUvSDH

=== INTERNAL URL ===
postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a:5432/ba_datn_db

=== EXTERNAL URL ===
postgresql://ba_datn_db_user:lNxER7uBbNYw60imnHx6coVCKyiUvSDH@dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db

=== JDBC URL (cho Spring Boot) ===
jdbc:postgresql://dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db
```

**Lưu ý**:
- Lưu file này ở nơi an toàn
- **KHÔNG** commit vào Git repository
- Có thể dùng password manager (1Password, LastPass, etc.)

---

## 5. Kiểm Tra Database

### Bước 1: Kiểm Tra Status

1. Vào Render Dashboard
2. Tìm database service
3. Kiểm tra status:
   - **Available** (màu xanh) ✅: Database đang chạy
   - **Creating** (màu vàng) ⏳: Đang tạo
   - **Failed** (màu đỏ) ❌: Có lỗi

### Bước 2: Kiểm Tra Connection

1. Vào database service → tab **"Connect"**
2. Render cung cấp **PSQL Command** để test connection
3. Copy command và chạy trong terminal (nếu có PostgreSQL client)

### Bước 3: Xem Metrics

1. Vào tab **"Metrics"** (nếu có)
2. Bạn sẽ thấy:
   - **Storage Used**: Dung lượng đã dùng
   - **Connections**: Số connections hiện tại
   - **Queries**: Số queries/giây

---

## 6. Kết Nối Từ Terminal (Tùy chọn)

### Yêu Cầu

- Cài đặt PostgreSQL client (`psql`)
- Hoặc dùng Docker để chạy psql

### Cách 1: Dùng psql Trực Tiếp

1. Copy **PSQL Command** từ Render Dashboard
2. Mở terminal
3. Paste và chạy command:

```bash
PGPASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH psql -h dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com -U ba_datn_db_user ba_datn_db
```

4. Nếu thành công, bạn sẽ thấy:
```
psql (15.x)
Type "help" for help.

ba_datn_db=>
```

5. Test query:
```sql
SELECT version();
```

6. Thoát:
```sql
\q
```

### Cách 2: Dùng Docker

Nếu không có psql, dùng Docker:

```bash
docker run -it --rm postgres:15 psql \
  -h dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com \
  -U ba_datn_db_user \
  -d ba_datn_db
```

Nhập password khi được hỏi.

---

## 7. Troubleshooting

### Lỗi: "Database creation failed"

**Triệu chứng**: 
- Status hiển thị "Failed"
- Có thông báo lỗi

**Giải pháp**:
1. Kiểm tra logs trong Render Dashboard
2. Thử tạo lại với tên khác
3. Kiểm tra region có available không
4. Liên hệ Render support nếu vẫn lỗi

### Lỗi: "Connection refused" khi kết nối

**Triệu chứng**:
```
psql: error: connection to server at "hostname" failed
```

**Giải pháp**:
1. Kiểm tra database status phải là "Available"
2. Kiểm tra hostname có đúng không (phải có domain `.region-postgres.render.com`)
3. Kiểm tra port (phải là 5432)
4. Kiểm tra firewall/network settings

### Lỗi: "Authentication failed"

**Triệu chứng**:
```
psql: error: password authentication failed
```

**Giải pháp**:
1. Kiểm tra username và password có đúng không
2. Copy lại password từ Render Dashboard (click "Show")
3. Nếu quên password, cần reset (sẽ mất dữ liệu nếu không có backup)

### Lỗi: "Database does not exist"

**Triệu chứng**:
```
psql: error: database "database_name" does not exist
```

**Giải pháp**:
1. Kiểm tra tên database có đúng không
2. Vào Render Dashboard → database service → tab "Info" để xem tên database chính xác
3. Dùng tên database đúng (có thể khác với tên service)

### Quên Password

**Nếu quên password**:

1. Vào database service → tab **"Settings"**
2. Tìm phần **"Reset Password"**
3. Click **"Reset Password"**
4. **⚠️ CẢNH BÁO**: 
   - Reset password sẽ **restart database**
   - Có thể mất connection tạm thời
   - Nếu không có backup, có thể mất dữ liệu

5. Sau khi reset, password mới sẽ hiển thị
6. **Lưu password mới ngay!**

**Khuyến nghị**:
- Lưu password ngay sau khi tạo database
- Dùng password manager
- Backup database định kỳ

---

## 📝 Checklist Tạo Database

Sử dụng checklist này để đảm bảo không bỏ sót bước nào:

### Trước Khi Tạo
- [ ] Đã đăng nhập Render Dashboard
- [ ] Đã quyết định tên database
- [ ] Đã chọn region
- [ ] Đã chọn plan (Free hoặc Paid)

### Khi Tạo
- [ ] Đã điền Name
- [ ] Đã điền Database (hoặc để mặc định)
- [ ] Đã điền User (hoặc để mặc định)
- [ ] Đã chọn Region
- [ ] Đã chọn PostgreSQL Version
- [ ] Đã chọn Plan
- [ ] Đã review lại tất cả thông tin
- [ ] Đã click "Create Database"

### Sau Khi Tạo
- [ ] Database status là "Available"
- [ ] Đã lưu Hostname
- [ ] Đã lưu Port (5432)
- [ ] Đã lưu Database Name
- [ ] Đã lưu Username
- [ ] Đã lưu Password (QUAN TRỌNG!)
- [ ] Đã lưu Internal Database URL
- [ ] Đã lưu External Database URL
- [ ] Đã lưu JDBC URL cho Spring Boot
- [ ] Đã test connection (nếu có thể)

---

## 🎉 Hoàn Thành!

Sau khi hoàn thành tất cả các bước trên, bạn đã có:

- ✅ PostgreSQL database trên Render
- ✅ Tất cả thông tin kết nối đã được lưu
- ✅ Database sẵn sàng để sử dụng với Spring Boot application

**Bước tiếp theo**: 
- Cấu hình Web Service với database này
- Xem [HUONG_DAN_RENDER_CHI_TIET.md](./HUONG_DAN_RENDER_CHI_TIET.md) để biết cách cấu hình Web Service

---

## 📚 Tài Liệu Tham Khảo

- [Render PostgreSQL Documentation](https://render.com/docs/databases)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Database Configuration](https://spring.io/guides/gs/accessing-data-jpa/)

**Chúc bạn tạo database thành công! 🚀**

