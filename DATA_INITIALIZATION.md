# Hướng dẫn Khởi tạo Dữ liệu Ban đầu

## Tổng quan

Hệ thống đã được cấu hình để tự động tạo dữ liệu ban đầu khi deploy lên Render với profile `prod`. Dữ liệu sẽ được tạo tự động khi:
- Database còn trống (chưa có dữ liệu)
- Ứng dụng chạy với profile `prod`
- Lần đầu tiên deploy lên Render

## Dữ liệu được tạo tự động

### 1. Chức vụ (ChucVu)
- **ADMIN**: Chức vụ quản trị viên
- **NHAN_VIEN**: Chức vụ nhân viên

### 2. Tài khoản Admin
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@datn.com`
- **Họ tên**: Administrator
- **Số điện thoại**: 0123456789
- **Chức vụ**: ADMIN
- **Trạng thái**: Hoạt động (1)

⚠️ **QUAN TRỌNG**: Sau khi deploy thành công, bạn nên:
1. Đăng nhập vào hệ thống với tài khoản admin
2. Đổi mật khẩu admin ngay lập tức
3. Cập nhật thông tin email và số điện thoại

### 3. Phương thức thanh toán (PhuongThucThanhToan)
- **Thanh toán khi nhận hàng (COD)**
- **Thanh toán online (VNPay)**

### 4. Dữ liệu mẫu

#### Thể loại (TheLoai)
- Giày thể thao
- Giày chạy bộ
- Giày da

#### Thương hiệu (ThuongHieu)
- Nike
- Adidas
- Puma

#### Màu sắc (MauSac)
- Đen (#000000)
- Trắng (#FFFFFF)
- Đỏ (#FF0000)
- Xanh dương (#0000FF)

#### Kích cỡ (KichCo)
- 36, 37, 38, 39, 40, 41, 42, 43

## Cách hoạt động

File `DataInitializer.java` trong package `datn.be.mycode.config` sẽ:
1. Kiểm tra xem database đã có dữ liệu chưa (kiểm tra số lượng chức vụ)
2. Nếu database trống → Tạo tất cả dữ liệu mẫu
3. Nếu database đã có dữ liệu → Bỏ qua, không tạo lại

## Logs

Khi deploy lên Render, bạn sẽ thấy logs như sau:

```
=== Bắt đầu khởi tạo dữ liệu ban đầu ===
Database trống, bắt đầu tạo dữ liệu mẫu...
Tạo chức vụ...
Đã tạo chức vụ: ADMIN
Đã tạo chức vụ: NHAN_VIEN
Tạo tài khoản Admin...
Đã tạo tài khoản Admin:
  - Username: admin
  - Password: admin123
  - Email: admin@datn.com
Tạo phương thức thanh toán...
Đã tạo phương thức: Thanh toán khi nhận hàng (COD)
Đã tạo phương thức: Thanh toán online (VNPay)
Tạo dữ liệu mẫu...
Đã tạo 3 thể loại mẫu
Đã tạo 3 thương hiệu mẫu
Đã tạo 4 màu sắc mẫu
Đã tạo 8 kích cỡ mẫu (36-43)
=== Hoàn thành khởi tạo dữ liệu ban đầu ===
```

## Tùy chỉnh dữ liệu

Nếu bạn muốn thay đổi dữ liệu mẫu, hãy chỉnh sửa file:
```
src/main/java/datn/be/mycode/config/DataInitializer.java
```

Sau đó commit và push lại lên GitHub để Render tự động deploy lại.

## Lưu ý

1. **Chỉ chạy một lần**: DataInitializer chỉ tạo dữ liệu khi database trống
2. **Profile prod**: Chỉ chạy khi `SPRING_PROFILES_ACTIVE=prod`
3. **Password đã được hash**: Mật khẩu admin được hash bằng BCrypt
4. **An toàn**: Nếu database đã có dữ liệu, sẽ không tạo lại để tránh trùng lặp

## Kiểm tra sau khi deploy

Sau khi deploy thành công, bạn có thể:

1. **Kiểm tra logs trên Render Dashboard** để xem dữ liệu đã được tạo chưa
2. **Đăng nhập với tài khoản admin**:
   - URL: `https://your-app.onrender.com/api/auth/login`
   - Username: `admin`
   - Password: `admin123`
3. **Kiểm tra database** qua Render Dashboard → PostgreSQL → Connect → Query Tool

## Troubleshooting

### Dữ liệu không được tạo
- Kiểm tra logs trên Render để xem có lỗi gì không
- Đảm bảo `SPRING_PROFILES_ACTIVE=prod` đã được set trong Environment Variables
- Kiểm tra xem database đã có dữ liệu chưa (nếu có thì sẽ không tạo lại)

### Muốn tạo lại dữ liệu
Nếu muốn tạo lại dữ liệu mẫu:
1. Xóa tất cả dữ liệu trong database (hoặc tạo database mới)
2. Restart service trên Render
3. DataInitializer sẽ tự động chạy lại

