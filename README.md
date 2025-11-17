# 🛍️ DATN Backend - Hệ Thống Quản Lý Bán Hàng

Backend API cho hệ thống quản lý bán hàng được xây dựng bằng Spring Boot.

## 📋 Mô Tả

Hệ thống quản lý bán hàng với đầy đủ các tính năng:
- Quản lý sản phẩm, đơn hàng, khách hàng
- Giỏ hàng và thanh toán (VNPay)
- Voucher và khuyến mãi
- Chat real-time (WebSocket)
- Đổi trả hàng
- Thống kê và báo cáo

## 🛠️ Công Nghệ Sử Dụng

- **Framework**: Spring Boot 3.2.5
- **Java**: 17
- **Database**: SQL Server
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI
- **Real-time**: WebSocket
- **Payment**: VNPay
- **Email**: Spring Mail
- **Build Tool**: Maven

## 📦 Cài Đặt

### Yêu Cầu

- Java 17+
- Maven 3.6+
- SQL Server
- Git

### Các Bước

1. **Clone repository**
```bash
git clone https://github.com/YOUR_USERNAME/ba_datn.git
cd ba_datn
```

2. **Cấu hình database**
- Copy file `src/main/resources/application.properties.example` thành `application.properties`
- Cập nhật thông tin kết nối database:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATN_SD40
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Cấu hình email** (tùy chọn)
- Cập nhật thông tin email trong `application.properties`

4. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

5. **Truy cập**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## 🔐 Bảo Mật

- JWT Authentication với Access Token (24h) và Refresh Token (7 ngày)
- Role-based Authorization: ADMIN, NHAN_VIEN, KHACH_HANG
- Mã hóa mật khẩu bằng BCrypt
- Security Audit logging

## 📚 API Documentation

Sau khi chạy ứng dụng, truy cập Swagger UI tại:
```
http://localhost:8080/swagger-ui.html
```

## 🗂️ Cấu Trúc Dự Án

```
src/main/java/datn/be/mycode/
├── config/          # Cấu hình (JWT, Security, WebSocket)
├── controller/      # AuthController
├── entity/          # JPA Entities
├── repository/      # JPA Repositories
├── RESTController/  # REST API Controllers
├── service/         # Business Logic
├── request/         # Request DTOs
└── response/        # Response DTOs
```

## 🔑 Phân Quyền

- **ADMIN**: Quản trị viên - toàn quyền
- **NHAN_VIEN**: Nhân viên - quản lý đơn hàng, sản phẩm
- **KHACH_HANG**: Khách hàng - mua hàng, xem đơn hàng

## 📝 Các Module Chính

- ✅ Quản lý sản phẩm
- ✅ Quản lý đơn hàng
- ✅ Giỏ hàng
- ✅ Voucher & Khuyến mãi
- ✅ Chat real-time
- ✅ Đổi trả hàng
- ✅ Thống kê

## 🚀 Deployment

Xem file `application-prod.properties` để cấu hình production.

## 📄 License

Dự án đồ án tốt nghiệp - DATN

## 👤 Tác Giả

Your Name

---

**Lưu ý**: File `application.properties` chứa thông tin nhạy cảm và không được commit lên Git. Sử dụng `application.properties.example` làm template.
