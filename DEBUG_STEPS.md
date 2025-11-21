# 🔍 Debug Steps - 401 Error Vẫn Xảy Ra

## ⚠️ QUAN TRỌNG: Cần Kiểm Tra Logs

Nếu vẫn bị 401, **PHẢI** xem logs để biết request đi đến đâu.

## Bước 1: Restart Application và Xem Logs Khi Start

**BẮT BUỘC** phải thấy logs này khi start:
```
🔧 [SecurityConfig] ========================================
🔧 [SecurityConfig] Building SecurityFilterChain...
🔧 [SecurityConfig] Configuring authorization rules...
✅ [SecurityConfig] /api/auth/** -> permitAll()
✅ [SecurityConfig] Authorization rules configured
✅ [SecurityConfig] /api/auth/** is PUBLIC (permitAll)
✅ [SecurityConfig] SecurityFilterChain built successfully
```

**Nếu KHÔNG thấy** → Application chưa restart hoặc code chưa được compile.

## Bước 2: Test Endpoint Đơn Giản

```bash
# Test GET endpoint (dễ hơn POST)
curl -v http://localhost:8080/api/auth/test

# Expected: 200 OK với message "Test endpoint works!"
```

**Nếu endpoint này cũng 401** → Vấn đề ở SecurityConfig, không phải ở login logic.

## Bước 3: Gọi Login Endpoint và Xem Logs

```bash
curl -v -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -d '{"username": "phuc", "pass": "123456"}'
```

### Logs BẮT BUỘC phải thấy:

1. **RequestLogger** (nếu có):
```
📥 [RequestLogger] INCOMING REQUEST
📥 [RequestLogger] Method: POST
📥 [RequestLogger] URI: /api/auth/login/nhan-vien
```

2. **JWT Filter**:
```
🔍 [JWT Filter] Processing: POST /api/auth/login/nhan-vien
🔍 [JWT Filter] Is public endpoint? true
✅ [JWT Filter] PUBLIC ENDPOINT DETECTED - SKIPPING JWT VALIDATION
```

3. **AuthController** (nếu request đến được):
```
✅ [AuthController] ===== LOGIN REQUEST RECEIVED =====
✅ [AuthController] Username: phuc
```

4. **Nếu thấy EntryPoint** (KHÔNG nên thấy):
```
❌ [SecurityConfig] AuthenticationEntryPoint called!
⚠️⚠️⚠️ [SecurityConfig] CRITICAL: EntryPoint called for /api/auth/** endpoint!
```

## Phân Tích Logs

### Case 1: KHÔNG thấy bất kỳ logs nào
→ Request không đến được server
- Kiểm tra app có đang chạy không
- Kiểm tra port (8080?)
- Kiểm tra URL có đúng không

### Case 2: Thấy RequestLogger nhưng KHÔNG thấy JWT Filter
→ Request bị chặn trước khi đến JWT Filter
- Có thể có filter khác đang chặn
- Có thể có vấn đề với Spring Security configuration

### Case 3: Thấy JWT Filter nhưng KHÔNG thấy AuthController
→ Request bị chặn sau JWT Filter
- SecurityConfig không hoạt động đúng
- permitAll() không được áp dụng

### Case 4: Thấy EntryPoint được gọi
→ **VẤN ĐỀ**: permitAll() KHÔNG hoạt động
- SecurityConfig có vấn đề
- Có thể có multiple SecurityConfig đang conflict

## Giải Pháp Tạm Thời (Để Test)

Nếu vẫn không work, thử tạm thời disable security cho tất cả endpoints:

```java
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

Nếu vẫn 401 → Vấn đề không phải ở SecurityConfig.

## Checklist Debug

- [ ] Application đã restart
- [ ] Logs `[SecurityConfig]` xuất hiện khi start
- [ ] Logs `[RequestLogger]` xuất hiện khi gọi endpoint
- [ ] Logs `[JWT Filter]` xuất hiện khi gọi endpoint
- [ ] Logs `[JWT Filter]` nói "PUBLIC ENDPOINT DETECTED"
- [ ] Logs `[AuthController]` xuất hiện khi gọi endpoint
- [ ] KHÔNG có logs `[SecurityConfig] AuthenticationEntryPoint called!`

## Gửi Logs Cho Tôi

Nếu vẫn không work, vui lòng gửi:
1. **Full logs** từ khi start application
2. **Full logs** khi gọi endpoint
3. **Response** từ curl command (headers + body)

