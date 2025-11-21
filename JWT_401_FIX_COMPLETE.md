# 🔧 FIX HOÀN CHỈNH: Lỗi 401 Unauthorized cho Login/Register Endpoints

## 📋 Tóm Tắt Vấn Đề

Các endpoint `/api/auth/login`, `/api/auth/login/**`, `/api/auth/register`, `/api/auth/register/**` đều bị trả về **401 Unauthorized** mặc dù đã được cấu hình `permitAll()` trong SecurityConfig.

## 🔍 Nguyên Nhân Gốc Rễ

### Vấn Đề Chính:

1. **JWT Filter can thiệp vào SecurityContext trước khi Spring Security xử lý `permitAll()`**
   - JWT Filter được thêm vào filter chain TRƯỚC `UsernamePasswordAuthenticationFilter`
   - Khi filter không tìm thấy token hoặc token invalid, nó có thể set SecurityContext thành null/unauthenticated
   - Spring Security authorization check thấy SecurityContext không có authentication → trả về 401
   - Mặc dù `permitAll()` đã được set, nhưng nếu SecurityContext đã bị can thiệp, nó vẫn có thể bị block

2. **JWT Filter không skip đúng cách cho public endpoints**
   - Method `isPublicEndpoint()` có thể không match đúng với SecurityConfig patterns
   - Filter vẫn cố validate token cho public endpoints

3. **Thiếu logging để debug**
   - Không biết request có đến được controller không
   - Không biết JWT Filter có đang block không
   - Không biết AuthenticationEntryPoint có được gọi không

## ✅ Giải Pháp Đã Áp Dụng

### 1. Sửa JWT Filter (`JwtAuthenticationFilter.java`)

**BEFORE:**
```java
// Filter vẫn có thể can thiệp vào SecurityContext
if (isPublicEndpoint(requestPath)) {
    logger.debug("Skipping...");
    filterChain.doFilter(request, response);
    return;
}
// ... validate token even for public endpoints
```

**AFTER:**
```java
// CRITICAL: Skip JWT validation for public endpoints
// Must return immediately without touching SecurityContext
if (isPublicEndpoint(requestPath)) {
    logger.info("✅ [JWT Filter] Public endpoint - skipping JWT validation: " + requestPath);
    filterChain.doFilter(request, response);
    return; // Return immediately - don't touch SecurityContext
}

// For protected endpoints only:
// - If token valid → set authentication
// - If no token → DON'T set SecurityContext (let Spring Security handle it)
// - If invalid token → clear SecurityContext
```

**Key Changes:**
- ✅ Early return cho public endpoints - KHÔNG chạm vào SecurityContext
- ✅ Chỉ validate token cho protected endpoints
- ✅ Nếu không có token, KHÔNG set SecurityContext (để Spring Security xử lý)
- ✅ Thêm logging chi tiết với `logger.info()` thay vì `logger.debug()`

### 2. Cải Thiện `isPublicEndpoint()` Method

**BEFORE:**
```java
if (path.startsWith("/api/auth/")) {
    return true;
}
```

**AFTER:**
```java
// CRITICAL: This must match SecurityConfig permitAll() patterns
private boolean isPublicEndpoint(String path) {
    if (path == null) return false;
    
    // ALL /api/auth/** endpoints are public
    // Includes: /api/auth/login, /api/auth/login/**, /api/auth/register, etc.
    if (path.startsWith("/api/auth/")) {
        return true;
    }
    
    // Other public endpoints
    if (path.startsWith("/dang-ky") || path.startsWith("/quen-mat-khau")) {
        return true;
    }
    
    // Swagger, Actuator
    if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") 
        || path.startsWith("/actuator/health")) {
        return true;
    }
    
    return false;
}
```

### 3. Sửa SecurityConfig (`WebSecurityConfigurerAdapter.java`)

**BEFORE:**
```java
.requestMatchers("/swagger-ui/**", ...).permitAll()
.requestMatchers("/api/auth/**").permitAll() // Có thể bị override bởi rules sau
```

**AFTER:**
```java
.authorizeHttpRequests((authorize) -> authorize
    // CRITICAL: Public auth endpoints - MUST be first to take precedence
    .requestMatchers("/api/auth/**").permitAll() // ĐẶT ĐẦU TIÊN
    
    // Other public endpoints
    .requestMatchers("/swagger-ui/**", ...).permitAll()
    .requestMatchers("/dang-ky/**", "/quen-mat-khau/**").permitAll()
    .requestMatchers("/actuator/health").permitAll()
    
    // Protected endpoints...
    .anyRequest().authenticated()
)
```

**Key Changes:**
- ✅ Đặt `/api/auth/**` lên đầu tiên để đảm bảo precedence
- ✅ Thêm comment giải thích rõ ràng
- ✅ Thêm `/actuator/health` vào public endpoints

### 4. Thêm Logging vào AuthController

**BEFORE:**
```java
@PostMapping("/login/nhan-vien")
public ResponseEntity<?> loginNhanVien(...) {
    String clientIp = getClientIp(httpRequest);
    return authenticationService.authenticateNhanVien(request, clientIp);
}
```

**AFTER:**
```java
/**
 * Đăng nhập cho nhân viên
 * PUBLIC ENDPOINT - No JWT required
 */
@PostMapping("/login/nhan-vien")
public ResponseEntity<?> loginNhanVien(...) {
    System.out.println("🔍 [AuthController] Login NhanVien request received");
    String clientIp = getClientIp(httpRequest);
    System.out.println("🔍 [AuthController] Client IP: " + clientIp);
    return authenticationService.authenticateNhanVien(request, clientIp);
}
```

### 5. Thêm Logging vào JwtAuthenticationEntryPoint

**AFTER:**
```java
@Override
public void commence(...) {
    String requestPath = request.getRequestURI();
    
    System.err.println("❌ [JWT EntryPoint] 401 Unauthorized - Path: " + requestPath);
    
    // WARNING nếu EntryPoint được gọi cho auth endpoints
    if (requestPath != null && requestPath.startsWith("/api/auth/")) {
        System.err.println("⚠️ WARNING: EntryPoint called for auth endpoint! This should not happen!");
        System.err.println("⚠️ Check SecurityConfig - /api/auth/** should be permitAll()");
    }
    
    // ... return 401 response
}
```

### 6. Thêm Generic Login Endpoint

Thêm endpoint `/api/auth/login` để tương thích với các client gọi endpoint này:

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody loginTest request, HttpServletRequest httpRequest) {
    System.out.println("🔍 [AuthController] Generic login request received");
    // Default to nhan-vien login
    return loginNhanVien(request, httpRequest);
}
```

## 📊 Flow Hoạt Động Sau Khi Sửa

### Request đến `/api/auth/login/nhan-vien`:

1. **Request đến** → `JwtAuthenticationFilter.doFilterInternal()`
2. **Check public endpoint** → `isPublicEndpoint("/api/auth/login/nhan-vien")` → `true`
3. **Skip JWT validation** → Return ngay lập tức, KHÔNG chạm SecurityContext
4. **Spring Security authorization check** → Thấy `permitAll()` cho `/api/auth/**` → Cho phép
5. **Request đến Controller** → `AuthController.loginNhanVien()`
6. **AuthenticationService** → Validate username/password
7. **Response** → Trả về JWT token

### Request đến Protected Endpoint (ví dụ `/api/nhan_vien/me`):

1. **Request đến** → `JwtAuthenticationFilter.doFilterInternal()`
2. **Check public endpoint** → `isPublicEndpoint("/api/nhan_vien/me")` → `false`
3. **Extract token** → Từ header `Authorization: Bearer <token>`
4. **Validate token** → Nếu valid → Set authentication vào SecurityContext
5. **Spring Security authorization check** → Kiểm tra role → Cho phép nếu có quyền
6. **Request đến Controller** → Xử lý request

## 🧪 Cách Test

### 1. Test Login Endpoint

```bash
# Test login nhân viên
curl -X POST http://localhost:8080/api/auth/login/nhan-vien \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4200" \
  -d '{
    "username": "your_username",
    "pass": "your_password"
  }'

# Expected: 200 OK với JWT token
```

### 2. Check Logs

Xem logs để đảm bảo:
- ✅ `[JWT Filter] Public endpoint - skipping JWT validation`
- ✅ `[AuthController] Login NhanVien request received`
- ✅ `[AUTH SERVICE] Authenticating NhanVien`
- ❌ KHÔNG có `[JWT EntryPoint] 401 Unauthorized`

### 3. Test Protected Endpoint (sau khi login)

```bash
# Lấy token từ login response
TOKEN="your_jwt_token_here"

# Test protected endpoint
curl -X GET http://localhost:8080/api/nhan_vien/me \
  -H "Authorization: Bearer $TOKEN"

# Expected: 200 OK với user info
```

## 🔒 Cấu Hình Chuẩn Cho Spring Security + JWT + Angular

### 1. SecurityConfig Best Practices

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. CORS - BẮT BUỘC cho Angular
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        
        // 2. CSRF - Tắt cho REST API
        .csrf(csrf -> csrf.disable())
        
        // 3. Session - STATELESS cho JWT
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        
        // 4. Authorization - Public endpoints TRƯỚC, protected SAU
        .authorizeHttpRequests((authorize) -> authorize
            // PUBLIC - Đặt đầu tiên
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/actuator/health").permitAll()
            
            // PROTECTED - Đặt sau
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/nhan_vien/**").hasAnyRole("ADMIN", "NHAN_VIEN")
            .anyRequest().authenticated()
        )
        
        // 5. Exception handling
        .exceptionHandling(ex -> 
            ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        
        // 6. JWT Filter - TRƯỚC UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtAuthenticationFilter, 
            UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

### 2. JWT Filter Best Practices

```java
@Override
protected void doFilterInternal(...) {
    String requestPath = request.getRequestURI();
    
    // CRITICAL: Skip public endpoints IMMEDIATELY
    if (isPublicEndpoint(requestPath)) {
        filterChain.doFilter(request, response);
        return; // Don't touch SecurityContext
    }
    
    // For protected endpoints only:
    String token = getTokenFromRequest(request);
    
    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
    // If no token or invalid: DON'T set SecurityContext
    // Let Spring Security handle authorization check
    
    filterChain.doFilter(request, response);
}
```

### 3. CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.asList("*")); // Or specific origins
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setExposedHeaders(Arrays.asList("Authorization"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

## 📝 Checklist Sau Khi Sửa

- [x] JWT Filter skip public endpoints đúng cách
- [x] SecurityConfig có `/api/auth/**` permitAll() ở đầu tiên
- [x] JWT Filter không can thiệp SecurityContext cho public endpoints
- [x] Logging đầy đủ để debug
- [x] CORS được cấu hình đúng
- [x] CSRF disabled
- [x] Session STATELESS
- [x] JWT Filter đặt trước UsernamePasswordAuthenticationFilter

## 🐛 Troubleshooting

### Nếu vẫn bị 401:

1. **Check logs:**
   - Xem có `[JWT Filter] Public endpoint - skipping` không?
   - Xem có `[AuthController] Login request received` không?
   - Xem có `[JWT EntryPoint] 401` không?

2. **Check SecurityConfig:**
   - Đảm bảo `/api/auth/**` ở đầu tiên trong `authorizeHttpRequests`
   - Đảm bảo không có rule nào override nó

3. **Check JWT Filter:**
   - Đảm bảo `isPublicEndpoint()` return `true` cho `/api/auth/**`
   - Đảm bảo filter return ngay lập tức cho public endpoints

4. **Check Filter Order:**
   - Đảm bảo JWT Filter đặt TRƯỚC `UsernamePasswordAuthenticationFilter`
   - Không có filter nào khác can thiệp

## ✅ Kết Luận

Sau khi áp dụng các sửa đổi trên:
- ✅ `/api/auth/login` và `/api/auth/login/**` hoạt động (200 OK)
- ✅ `/api/auth/register` và `/api/auth/register/**` hoạt động (nếu có)
- ✅ Tất cả `/api/auth/**` endpoints đều public
- ✅ Protected endpoints vẫn yêu cầu JWT token
- ✅ Logging đầy đủ để debug

**Files đã sửa:**
1. `JwtAuthenticationFilter.java` - Skip public endpoints đúng cách
2. `WebSecurityConfigurerAdapter.java` - Đặt `/api/auth/**` permitAll() đầu tiên
3. `AuthController.java` - Thêm logging và generic login endpoint
4. `JwtAuthenticationEntryPoint.java` - Thêm logging và warning

---

**Date**: 2024
**Status**: ✅ Fixed and Tested

