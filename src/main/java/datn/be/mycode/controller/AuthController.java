package datn.be.mycode.controller;

import datn.be.mycode.entity.loginTest;
import datn.be.mycode.response.MessageResponse;
import datn.be.mycode.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Đăng nhập cho khách hàng
     * PUBLIC ENDPOINT - No JWT required
     */
    @PostMapping("/login/khach-hang")
    public ResponseEntity<?> loginKhachHang(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        System.out.println("🔍 [AuthController] Login KhachHang request received");
        String clientIp = getClientIp(httpRequest);
        System.out.println("🔍 [AuthController] Client IP: " + clientIp);
        return authenticationService.authenticateKhachHang(request, clientIp);
    }
    
    /**
     * Đăng nhập cho nhân viên
     * PUBLIC ENDPOINT - No JWT required
     */
    @PostMapping("/login/nhan-vien")
    public ResponseEntity<?> loginNhanVien(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        System.out.println("========================================");
        System.out.println("✅ [AuthController] ===== LOGIN REQUEST RECEIVED =====");
        System.out.println("✅ [AuthController] Endpoint: POST /api/auth/login/nhan-vien");
        System.out.println("✅ [AuthController] Username: " + (request != null ? request.getUsername() : "null"));
        System.out.println("✅ [AuthController] Password: " + (request != null && request.getPass() != null ? "***" : "null"));
        String clientIp = getClientIp(httpRequest);
        System.out.println("✅ [AuthController] Client IP: " + clientIp);
        System.out.println("✅ [AuthController] Request URI: " + httpRequest.getRequestURI());
        System.out.println("========================================");
        
        if (request == null) {
            System.err.println("❌ [AuthController] Request body is NULL!");
            return ResponseEntity.badRequest().body("Request body is required");
        }
        
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            System.err.println("❌ [AuthController] Username is NULL or empty!");
            return ResponseEntity.badRequest().body("Username is required");
        }
        
        return authenticationService.authenticateNhanVien(request, clientIp);
    }
    
    /**
     * Generic login endpoint (for compatibility)
     * PUBLIC ENDPOINT - No JWT required
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        System.out.println("🔍 [AuthController] Generic login request received");
        // Default to nhan-vien login, can be customized
        return loginNhanVien(request, httpRequest);
    }
    
    /**
     * Test endpoint to verify SecurityConfig is working
     * PUBLIC ENDPOINT - No JWT required
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        System.out.println("✅ [AuthController] Test endpoint called - SecurityConfig is working!");
        return ResponseEntity.ok(new MessageResponse("Test endpoint works! SecurityConfig is configured correctly."));
    }
    
    /**
     * Refresh access token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.getRefreshToken());
    }
    
    /**
     * Đăng xuất
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // TODO: Implement token blacklist logic if needed
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công"));
    }
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        // TODO: Return current user info from SecurityContext
        return ResponseEntity.ok(new MessageResponse("User authenticated"));
    }
    
    /**
     * Lấy IP address của client
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    // Request DTOs
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}

