# ✅ Kiểm Tra Environment Variables

## ❌ Các Vấn Đề Cần Sửa

### 1. SPRING_DATASOURCE_PASSWORD - SAI

**Hiện tại:**
```
SPRING_DATASOURCE_PASSWORD=1NxER7uBbNYw60imnHx6coVCKyiUvSDH
```

**Đúng phải là:**
```
SPRING_DATASOURCE_PASSWORD=lNxER7uBbNYw60imnHx6coVCKyiUvSDH
```

⚠️ **Thiếu chữ "l" (chữ L thường) ở đầu!**

### 2. MAIL_PASSWORD - Có thể sai

**Hiện tại:**
```
MAIL_PASSWORD=Phucpanda1226@
```

⚠️ **Vấn đề**: Đây có vẻ là password thường của Gmail, không phải **Gmail App Password**.

**Gmail App Password:**
- Phải là 16 ký tự
- Không có ký tự đặc biệt như `@`
- Ví dụ: `abcd efgh ijkl mnop` (16 ký tự, không có dấu cách khi dùng)

**Cách tạo Gmail App Password:**
1. Vào: https://myaccount.google.com/security
2. Bật **2-Step Verification** (nếu chưa bật)
3. Vào: https://myaccount.google.com/apppasswords
4. Chọn **"Mail"** và **"Other (Custom name)"**
5. Nhập tên: `Render`
6. Click **"Generate"**
7. Copy password 16 ký tự (bỏ dấu cách khi paste vào Render)

**Nếu không cần gửi email**, có thể xóa các biến:
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

### 3. NEW_SECRET - Không cần thiết

**Hiện tại:**
```
NEW_SECRET=131b8a323a26dae867db423cb50a169b
```

⚠️ **Biến này không được sử dụng trong code**, có thể **XÓA**.

---

## ✅ Các Biến Đúng

Các biến sau đã đúng:

- ✅ `HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect`
- ✅ `JWT_EXPIRATION=86400000`
- ✅ `JWT_REFRESH_EXPIRATION=604800000`
- ✅ `JWT_SECRET=GP3PLcUTTgmdYi5jVt7p6PQ+Ttd5wStcfrRFFswGf1U=` (đủ 32 ký tự)
- ✅ `MAIL_HOST=smtp.gmail.com`
- ✅ `MAIL_PORT=587`
- ✅ `MAIL_USERNAME=phucpanda2003@gmail.com`
- ✅ `PORT=8080`
- ✅ `SPRING_DATASOURCE_DRIVER=org.postgresql.Driver`
- ✅ `SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com:5432/ba_datn_db`
- ✅ `SPRING_DATASOURCE_USERNAME=ba_datn_db_user`
- ✅ `SPRING_PROFILES_ACTIVE=prod`

---

## 🔧 Cần Sửa Ngay

### 1. Sửa SPRING_DATASOURCE_PASSWORD

Trên Render Dashboard:
1. Vào Web Service → tab **"Environment"**
2. Tìm `SPRING_DATASOURCE_PASSWORD`
3. Click **"Edit"** hoặc **"Update"**
4. Đổi từ: `1NxER7uBbNYw60imnHx6coVCKyiUvSDH`
5. Thành: `lNxER7uBbNYw60imnHx6coVCKyiUvSDH`
6. Click **"Save"**

### 2. Sửa hoặc Xóa MAIL_PASSWORD

**Nếu cần gửi email:**
1. Tạo Gmail App Password (xem hướng dẫn trên)
2. Update `MAIL_PASSWORD` với App Password mới

**Nếu không cần gửi email:**
1. Xóa các biến: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`

### 3. Xóa NEW_SECRET

1. Vào tab **"Environment"**
2. Tìm `NEW_SECRET`
3. Click **"Delete"** hoặc icon thùng rác
4. Confirm delete

---

## ✅ Sau Khi Sửa

Sau khi sửa xong, **deploy lại** service:
1. Vào Web Service
2. Click **"Manual Deploy"** → **"Deploy latest commit"**
3. Hoặc đợi auto-deploy (nếu đã bật)

---

## 🧪 Test Sau Khi Sửa

Sau khi deploy lại, kiểm tra:

1. **Health check:**
```bash
curl https://ba-datn-backend.onrender.com/actuator/health
```

2. **Xem logs:**
- Vào tab **"Logs"** trên Render Dashboard
- Kiểm tra không có lỗi database connection
- Kiểm tra DataInitializer đã chạy thành công

3. **Test login:**
```bash
curl -X POST https://ba-datn-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

