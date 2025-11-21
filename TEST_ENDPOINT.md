# 🧪 Test Endpoint - Debug 401 Error

## Bước 1: Kiểm Tra Application Đã Start

```bash
# Kiểm tra app có đang chạy không
curl http://localhost:8080/actuator/health

# Hoặc
curl http://localhost:8080/api/auth/test
```

## Bước 2: Xem Logs Khi Start Application

Bạn PHẢI thấy:
```
🔧 [SecurityConfig] ========================================
🔧 [SecurityConfig] Building SecurityFilterChain...
🔧 [SecurityConfig] Configuring authorization rules...
✅ [SecurityConfig] /api/auth/** -> permitAll()
✅ [SecurityConfig] Authorization rules configured
✅ [SecurityConfig] /api/auth/** is PUBLIC (permitAll)
✅ [SecurityConfig] SecurityFilterChain built successfully
✅ [SecurityConfig] JWT Filter added BEFORE UsernamePasswordAuthenticationFilter
🔧 [SecurityConfig] ========================================
```

## Bước 3: Test Login Endpoint

```bash
curl -v -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{
    "username": "phuc",
    "pass": "123456"
  }'
```

## Bước 4: Xem Logs Khi Gọi Endpoint

Bạn PHẢI thấy:
```
🔍 [JWT Filter] ========================================
🔍 [JWT Filter] Processing: POST /api/auth/login/nhan-vien
🔍 [JWT Filter] Context Path: 
🔍 [JWT Filter] Servlet Path: /api/auth/login/nhan-vien
🔍 [JWT Filter] Is public endpoint? true
✅ [JWT Filter] Public endpoint - skipping JWT validation: /api/auth/login/nhan-vien
✅ [JWT Filter] Continuing filter chain WITHOUT JWT validation
========================================
✅ [AuthController] ===== LOGIN REQUEST RECEIVED =====
✅ [AuthController] Endpoint: POST /api/auth/login/nhan-vien
✅ [AuthController] Username: phuc
✅ [AuthController] Password: ***
```

## Nếu KHÔNG Thấy Logs

### Không thấy `[SecurityConfig]` logs:
- Application chưa restart
- Code chưa được compile
- Có lỗi khi start application

### Không thấy `[JWT Filter]` logs:
- Request không đến được filter
- Có thể bị chặn bởi filter khác
- Port sai hoặc context path khác

### Không thấy `[AuthController]` logs:
- Request bị chặn bởi Spring Security
- SecurityConfig không hoạt động đúng
- Endpoint mapping sai

## Debug Commands

```bash
# 1. Kiểm tra app có chạy không
curl http://localhost:8080/actuator/health

# 2. Test endpoint đơn giản
curl http://localhost:8080/api/auth/test

# 3. Test login với verbose
curl -v -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username": "phuc", "pass": "123456"}'

# 4. Kiểm tra logs trong console/terminal
# Xem có logs [SecurityConfig], [JWT Filter], [AuthController] không
```

## Checklist

- [ ] Application đã restart
- [ ] Logs `[SecurityConfig]` xuất hiện khi start
- [ ] Logs `[JWT Filter]` xuất hiện khi gọi endpoint
- [ ] Logs `[AuthController]` xuất hiện khi gọi endpoint
- [ ] Response không phải 401
- [ ] Response không có path `/error`

## Nếu Vẫn 401

1. **Copy toàn bộ logs** từ khi start app
2. **Copy response** từ curl command
3. **Kiểm tra** xem có exception nào không
4. **Gửi cho tôi** để phân tích tiếp

