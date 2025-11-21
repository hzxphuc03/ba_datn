# 🐛 Debug 401 Error - Path "/error"

## Vấn Đề

Response trả về path `/error` thay vì path thực tế, cho thấy request bị redirect đến Spring Boot error handler.

## Các Bước Debug

### 1. Kiểm Tra Logs

Xem logs để tìm:
- `[JWT Filter] Processing: ...` - Request có đến filter không?
- `[JWT Filter] Public endpoint - skipping` - Filter có skip đúng không?
- `[AuthController] Login request received` - Request có đến controller không?
- `[JWT EntryPoint] 401 Unauthorized` - EntryPoint có được gọi không?

### 2. Kiểm Tra Request Path

Đảm bảo bạn đang gọi đúng endpoint:
- ✅ `/api/auth/login/nhan-vien`
- ✅ `/api/auth/login/khach-hang`
- ❌ KHÔNG phải `/api/auth/login/nhan-vien/` (có trailing slash)

### 3. Kiểm Tra HTTP Method

Đảm bảo dùng POST:
```bash
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "pass": "test"}'
```

### 4. Kiểm Tra CORS Preflight

Nếu gọi từ browser, có thể cần xử lý OPTIONS request:
```bash
# Test OPTIONS request
curl -X OPTIONS http://localhost:8080/api/auth/login/nhan-vien \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

### 5. Kiểm Tra Multiple SecurityFilterChain

Có thể có nhiều SecurityFilterChain đang conflict. Kiểm tra:
```bash
# Tìm tất cả SecurityFilterChain
grep -r "SecurityFilterChain" src/main/java/
```

### 6. Test Trực Tiếp

Test endpoint mà không qua filter:
```bash
# Test health endpoint (should work)
curl http://localhost:8080/actuator/health

# Test login endpoint
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "pass": "test"}' \
  -v
```

## Giải Pháp Đã Áp Dụng

1. ✅ Đổi `@Order(2)` thành `@Order(1)` để đảm bảo chain được xử lý đầu tiên
2. ✅ Thêm logging vào SecurityConfig để debug
3. ✅ Thêm System.out.println vào JWT Filter để thấy logs ngay lập tức
4. ✅ Đảm bảo JWT Filter skip public endpoints đúng cách

## Next Steps

1. Restart application
2. Xem logs khi gọi endpoint
3. Kiểm tra xem có logs `[JWT Filter]` và `[AuthController]` không
4. Nếu không có logs → Request không đến được filter/controller
5. Nếu có logs nhưng vẫn 401 → Kiểm tra SecurityConfig

