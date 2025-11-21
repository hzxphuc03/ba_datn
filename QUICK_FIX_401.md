# 🚨 QUICK FIX: 401 Error với Path "/error"

## ⚡ Các Thay Đổi Đã Áp Dụng

1. ✅ Đổi `@Order(2)` → `@Order(1)` trong SecurityConfig
2. ✅ Thêm logging chi tiết vào SecurityConfig, JWT Filter, và EntryPoint
3. ✅ Thêm test endpoint `/api/auth/test` để verify SecurityConfig

## 🔍 Các Bước Debug Ngay

### Bước 1: Restart Application

```bash
# Stop và restart application
# Nếu dùng Docker:
docker-compose -f docker-compose.local.yml restart app

# Nếu chạy local:
# Stop app và start lại
```

### Bước 2: Test Endpoint Đơn Giản

```bash
# Test endpoint đơn giản (GET request)
curl http://localhost:8080/api/auth/test

# Expected: 200 OK với message "Test endpoint works!"
```

### Bước 3: Kiểm Tra Logs

Khi start application, bạn sẽ thấy:
```
🔧 [SecurityConfig] Building SecurityFilterChain...
🔧 [SecurityConfig] Configuring authorization rules...
✅ [SecurityConfig] Authorization rules configured
✅ [SecurityConfig] SecurityFilterChain built successfully
```

Khi gọi endpoint, bạn sẽ thấy:
```
🔍 [JWT Filter] Processing: POST /api/auth/login/nhan-vien
✅ [JWT Filter] Public endpoint - skipping JWT validation: /api/auth/login/nhan-vien
🔍 [AuthController] Login NhanVien request received
```

### Bước 4: Test Login Endpoint

```bash
# Test với curl
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username": "your_username", "pass": "your_password"}' \
  -v

# Kiểm tra:
# - Status code: 200 (not 401)
# - Response có JWT token
# - Logs có "[AuthController] Login request received"
```

## 🐛 Nếu Vẫn Bị 401

### Kiểm Tra 1: Request Path

Đảm bảo path đúng:
- ✅ `/api/auth/login/nhan-vien`
- ❌ KHÔNG có trailing slash: `/api/auth/login/nhan-vien/`
- ❌ KHÔNG có context path: `/myapp/api/auth/login/nhan-vien`

### Kiểm Tra 2: HTTP Method

Đảm bảo dùng POST:
```bash
# ĐÚNG
curl -X POST http://localhost:8080/api/auth/login/nhan-vien ...

# SAI
curl -X GET http://localhost:8080/api/auth/login/nhan-vien ...
```

### Kiểm Tra 3: Content-Type Header

Đảm bảo có header:
```bash
-H "Content-Type: application/json"
```

### Kiểm Tra 4: Request Body Format

Đảm bảo JSON đúng format:
```json
{
  "username": "your_username",
  "pass": "your_password"
}
```

### Kiểm Tra 5: Xem Logs Chi Tiết

Nếu EntryPoint được gọi, bạn sẽ thấy:
```
❌ [JWT EntryPoint] 401 Unauthorized - Path: POST /api/auth/login/nhan-vien
⚠️ [JWT EntryPoint] WARNING: EntryPoint called for auth endpoint!
```

Nếu thấy warning này → SecurityConfig không hoạt động đúng.

## 🔧 Giải Pháp Nếu Vẫn Lỗi

### Option 1: Kiểm Tra Có Multiple SecurityConfig

```bash
# Tìm tất cả SecurityConfig
find src/main/java -name "*Security*.java" -type f
```

Nếu có nhiều file, có thể đang conflict.

### Option 2: Tạm Thời Disable Security

Để test xem có phải do SecurityConfig không:

```java
// Trong WebSecurityConfigurerAdapter, tạm thời:
.authorizeHttpRequests((authorize) -> authorize
    .anyRequest().permitAll() // Tạm thời cho phép tất cả
)
```

Nếu vẫn 401 → Vấn đề không phải ở SecurityConfig.

### Option 3: Kiểm Tra Application Properties

Đảm bảo không có config nào override:
```properties
# Kiểm tra application.properties
# Không nên có:
# spring.security.*
```

### Option 4: Kiểm Tra Dependencies

Đảm bảo Spring Security version đúng:
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## 📊 Expected Logs Flow

### Khi Application Start:
```
🔧 [SecurityConfig] Building SecurityFilterChain...
🔧 [SecurityConfig] Configuring authorization rules...
✅ [SecurityConfig] Authorization rules configured
🔧 [SecurityConfig] Configuring exception handling...
✅ [SecurityConfig] SecurityFilterChain built successfully
```

### Khi Gọi Login Endpoint:
```
🔍 [JWT Filter] Processing: POST /api/auth/login/nhan-vien
✅ [JWT Filter] Public endpoint - skipping JWT validation: /api/auth/login/nhan-vien
🔍 [AuthController] Login NhanVien request received
🔍 [AuthController] Client IP: 127.0.0.1
🔍 [AUTH SERVICE] Authenticating NhanVien: username
✅ User found: username
🔐 Checking password...
   Password matches: true
✅ Login successful for: username
```

### Nếu Bị 401 (KHÔNG nên thấy):
```
❌ [JWT EntryPoint] 401 Unauthorized - Path: POST /api/auth/login/nhan-vien
⚠️ [JWT EntryPoint] WARNING: EntryPoint called for auth endpoint!
```

## ✅ Checklist

- [ ] Application đã restart
- [ ] Logs có "[SecurityConfig] Building SecurityFilterChain..."
- [ ] Test endpoint `/api/auth/test` trả về 200
- [ ] Logs có "[JWT Filter] Processing" khi gọi login
- [ ] Logs có "[JWT Filter] Public endpoint - skipping"
- [ ] Logs có "[AuthController] Login request received"
- [ ] KHÔNG có "[JWT EntryPoint] 401" cho auth endpoints

## 🆘 Nếu Vẫn Không Work

1. **Copy toàn bộ logs** từ khi start app đến khi gọi endpoint
2. **Copy request** (curl command hoặc browser network tab)
3. **Kiểm tra** xem có exception nào trong logs không
4. **Test** endpoint `/api/auth/test` trước

---

**Lưu ý**: Path `/error` thường xuất hiện khi Spring Boot error handler được gọi. Điều này có nghĩa là có exception xảy ra hoặc request bị block trước khi đến controller.

