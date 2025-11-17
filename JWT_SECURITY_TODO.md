# CÁC PHẦN CẦN LÀM ĐỂ BẢO MẬT JWT

## 📋 TÓM TẮT DỰ ÁN
- **Hệ thống**: Quản lý bán hàng (DATN)
- **Loại người dùng**: 
  - `ADMIN` (Admin)
  - `NHAN_VIEN` (Nhân viên) 
  - `KHACH_HANG` (Khách hàng)
- **JWT hiện tại**: Đã có token generation nhưng chưa có validation filter

---

## ✅ ĐÃ HOÀN THÀNH

1. ✅ **JwtTokenProvider** - Đã cập nhật với API mới (0.12.5)
2. ✅ **Dependencies** - Đã cài đặt Spring Security và JWT libraries
3. ✅ **Token Generation** - Đã có trong LoginController (NhanVienService, KhachHangService)

---

## 🔴 CẦN LÀM NGAY

### 1. **Tạo JWT Authentication Filter** ⚠️ QUAN TRỌNG
**File**: `src/main/java/datn/be/mycode/config/JwtAuthenticationFilter.java`

**Chức năng**:
- Intercept mọi HTTP request
- Lấy token từ header `Authorization: Bearer <token>`
- Validate token bằng JwtTokenProvider
- Set authentication vào SecurityContext
- Cho phép request tiếp tục nếu không có token (public endpoint)

**Lưu ý**: 
- Không throw exception cho public endpoints
- Chỉ validate và set authentication nếu có token hợp lệ

---

### 2. **Tạo JWT Entry Point** ⚠️ QUAN TRỌNG
**File**: `src/main/java/datn/be/mycode/config/JwtAuthenticationEntryPoint.java`

**Chức năng**:
- Xử lý khi unauthenticated user truy cập protected endpoint
- Trả về JSON error 401 với message tiếng Việt
- Format: `{"status": 401, "error": "Unauthorized", "message": "..."}`

---

### 3. **Cập nhật WebSecurityConfig** ⚠️ QUAN TRỌNG
**File**: `src/main/java/datn/be/mycode/config/WebSecurityConfigurerAdapter.java`

**Cần làm**:
- Thêm JWT filter vào filter chain
- Cấu hình phân quyền theo endpoint pattern:
  - **Public**: `/api/auth/**`, `/dang-ky/**`, `/quen-mat-khau/**`
  - **Admin only**: `/api/admin/**`, các endpoint quản lý hệ thống
  - **Nhân viên + Admin**: `/api/nhan-vien/**`, `/api/hoa_don/**`, `/api/thong-ke/**`
  - **Khách hàng**: `/api/gio_hang/**`, `/api/khach_hang/{id}/**` (chỉ của chính họ)
- Enable `@PreAuthorize` với `@EnableMethodSecurity`

---

### 4. **Sửa JwtTokenProvider - Role Mapping** ⚠️ QUAN TRỌNG
**File**: `src/main/java/datn/be/mycode/config/JwtTokenProvider.java`

**Vấn đề hiện tại**:
- Role trong database có thể là: "Admin", "Nhân viên", "Quản lý", "KhachHang"
- Spring Security cần format: `ROLE_ADMIN`, `ROLE_NHAN_VIEN`, `ROLE_KHACH_HANG`

**Cần làm**:
- Thêm method `mapRoleToSpringRole(String dbRole)` để convert:
  ```java
  "Admin" -> "ROLE_ADMIN"
  "Nhân viên", "Quản lý", "Nhân Viên" -> "ROLE_NHAN_VIEN"
  "KhachHang", "Khách hàng" -> "ROLE_KHACH_HANG"
  ```
- Sử dụng trong `getAuthentication()` method

---

### 5. **Cập nhật Service Layer - Role Mapping**
**Files**: 
- `src/main/java/datn/be/mycode/service/NhanVienService.java`
- `src/main/java/datn/be/mycode/service/KhachHangService.java`

**Cần làm**:
- Khi generate token, map role từ database thành format chuẩn
- Đảm bảo role trong token là: "ADMIN", "NHAN_VIEN", hoặc "KHACH_HANG"
- Có thể tạo helper method trong JwtTokenProvider

---

## 🟡 PHÂN QUYỀN ENDPOINTS

### **PUBLIC ENDPOINTS** (Không cần authentication)
```
/api/auth/**
/dang-ky/**
/quen-mat-khau/**
/swagger-ui/**
/v3/api-docs/**
/api/the_loai/** (GET - xem danh sách)
/api/san_pham/** (GET - xem sản phẩm)
/api/su_kien/get-su-kien-active (GET)
```

### **ADMIN ONLY**
```
/api/admin/**
/api/nhan_vien/** (POST, PUT, DELETE)
/api/chuc_vu/**
/api/thong_ke/** (toàn bộ)
/api/su_kien/** (POST, PUT, DELETE)
/api/voucher/** (POST, PUT, DELETE)
```

### **NHÂN VIÊN + ADMIN**
```
/api/hoa_don/** (toàn bộ CRUD)
/api/nhan_tin/**
/api/doi_tra/** (xử lý đổi trả)
/api/khach_hang/** (xem danh sách, chi tiết)
/api/ban_hang/** (bán hàng tại quầy)
```

### **KHÁCH HÀNG**
```
/api/gio_hang/{idKhachHang}/** (chỉ của chính họ)
/api/hoa_don/get-hoa-don-by-khach-hang (chỉ của chính họ)
/api/dia_chi/{idKhachHang}/** (chỉ của chính họ)
/api/khach_hang/{id} (chỉ xem của chính mình)
/api/danh_gia/** (đánh giá sản phẩm)
```

### **KHÁCH HÀNG HOẶC NHÂN VIÊN**
```
/api/gio_hang/add**
/api/gio_hang/thanh-toan**
```

---

## 🟢 CẢI THIỆN BẢO MẬT (OPTIONAL)

### 6. **Method Level Security**
**Cần làm**: Thêm `@PreAuthorize` vào các controller methods quan trọng:

**Ví dụ**:
```java
// KhachHangController
@PreAuthorize("hasRole('ADMIN') or hasRole('NHAN_VIEN')")
@GetMapping("")

@PreAuthorize("hasRole('ADMIN') or hasRole('NHAN_VIEN') or (hasRole('KHACH_HANG') and #id == authentication.principal.id)")
@GetMapping("/{id}")
```

### 7. **Token Refresh Mechanism**
- Thêm refresh token endpoint
- Rotate access token
- Blacklist expired tokens

### 8. **Rate Limiting**
- Giới hạn số lần login/giờ
- Giới hạn API calls từ một IP

### 9. **CORS Configuration** 
**File**: `WebConfig.java`
- Đã có nhưng cần verify
- Đảm bảo allow `Authorization` header

### 10. **Logging & Monitoring**
- Log các lần authentication failed
- Monitor suspicious activities
- Track token usage

---

## 📝 KIỂM TRA SAU KHI IMPLEMENT

### Test Cases:
1. ✅ Login trả về token hợp lệ
2. ✅ Public endpoint không cần token
3. ✅ Protected endpoint yêu cầu token
4. ✅ Invalid token trả về 401
5. ✅ Expired token trả về 401
6. ✅ Admin có thể truy cập admin endpoints
7. ✅ Nhân viên KHÔNG thể truy cập admin endpoints
8. ✅ Khách hàng chỉ xem được dữ liệu của chính họ
9. ✅ Role mapping đúng trong token

---

## 🚀 THỨ TỰ THỰC HIỆN

**Bước 1**: Tạo JwtAuthenticationFilter và JwtAuthenticationEntryPoint
**Bước 2**: Cập nhật WebSecurityConfig (thêm filter vào chain)
**Bước 3**: Sửa role mapping trong JwtTokenProvider
**Bước 4**: Test với một endpoint đơn giản
**Bước 5**: Cấu hình phân quyền từng nhóm endpoint
**Bước 6**: Thêm @PreAuthorize vào các method quan trọng
**Bước 7**: Test toàn bộ flow và fix bugs

---

## 📚 TÀI LIỆU THAM KHẢO

- File có sẵn trong `/security-improvements/` folder
- JWT Integration Guide: `JWT_INTEGRATION_GUIDE.md`
- Frontend examples: `/frontend-examples/`

---

**Lưu ý quan trọng**: 
- ⚠️ Hiện tại tất cả endpoints đang `permitAll()` → cần thay đổi ngay
- ⚠️ Token đã được generate nhưng chưa được validate trong filter
- ⚠️ Role mapping cần chuẩn hóa giữa database và Spring Security

