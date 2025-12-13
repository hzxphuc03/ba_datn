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
// CORS được cấu hình globally trong WebConfig, không cần @CrossOrigin ở đây
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Đăng nhập cho khách hàng
     */
    @PostMapping("/login/khach-hang")
    public ResponseEntity<?> loginKhachHang(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"F\",\"location\":\"AuthController.java:26\",\"message\":\"loginKhachHang controller method called\",\"data\":{\"path\":\"" + httpRequest.getServletPath() + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        String clientIp = getClientIp(httpRequest);
        return authenticationService.authenticateKhachHang(request, clientIp);
    }
    
    /**
     * Đăng nhập cho nhân viên
     */
    @PostMapping("/login/nhan-vien")
    public ResponseEntity<?> loginNhanVien(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        return authenticationService.authenticateNhanVien(request, clientIp);
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

