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
     */
    @PostMapping("/login/khach-hang")
    public ResponseEntity<?> loginKhachHang(@RequestBody loginTest request, HttpServletRequest httpRequest) {
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
     * Đổi mật khẩu
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        // TODO: Implement change password logic
        return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công"));
    }
    
    /**
     * Quên mật khẩu - Gửi OTP
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // TODO: Implement forgot password logic
        return ResponseEntity.ok(new MessageResponse("OTP đã được gửi đến email của bạn"));
    }
    
    /**
     * Reset mật khẩu với OTP
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // TODO: Implement reset password logic
        return ResponseEntity.ok(new MessageResponse("Reset mật khẩu thành công"));
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
    
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class ForgotPasswordRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class ResetPasswordRequest {
        private String email;
        private String otp;
        private String newPassword;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}