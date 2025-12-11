package datn.be.mycode.service;

import datn.be.mycode.config.JwtTokenProvider;
import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.entity.NhanVien;
import datn.be.mycode.entity.loginTest;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.response.UserLoginResponse;
import datn.be.mycode.response.erroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {
    
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordService passwordService;
    private final SecurityAuditService auditService;
    
    public AuthenticationService(KhachHangRepository khachHangRepository,
                               NhanVienRepository nhanVienRepository,
                               JwtTokenProvider jwtTokenProvider,
                               PasswordService passwordService,
                               SecurityAuditService auditService) {
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordService = passwordService;
        this.auditService = auditService;
    }
    
    public ResponseEntity<?> authenticateKhachHang(loginTest loginRequest, String clientIp) {
        try {
            System.out.println("========================================");
            System.out.println("🔍 [AUTH SERVICE] Authenticating KhachHang");
            System.out.println("🔍 [AUTH SERVICE] Username: " + loginRequest.getUsername());
            System.out.println("🔍 [AUTH SERVICE] Password provided: " + (loginRequest.getPass() != null ? "YES (length: " + loginRequest.getPass().length() + ")" : "NO"));
            System.out.println("🔍 [AUTH SERVICE] Client IP: " + clientIp);
            
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                System.err.println("❌ [AUTH SERVICE] Username is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new erroResponse("Tài khoản không được để trống"));
            }
            
            if (loginRequest.getPass() == null || loginRequest.getPass().trim().isEmpty()) {
                System.err.println("❌ [AUTH SERVICE] Password is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new erroResponse("Mật khẩu không được để trống"));
            }
            
            // Tìm khách hàng theo tài khoản
            System.out.println("🔍 [AUTH SERVICE] Searching for user with taiKhoan: " + loginRequest.getUsername() + ", trangThai: 1");
            Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByTaiKhoanAndTrangThai(loginRequest.getUsername().trim(), 1);
            
            if (khachHangOpt.isEmpty()) {
                System.out.println("❌ [AUTH SERVICE] User not found: " + loginRequest.getUsername());
                System.out.println("   ⚠️ User not found with taiKhoan: " + loginRequest.getUsername() + " and trangThai: 1");
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            KhachHang khachHang = khachHangOpt.get();
            System.out.println("✅ User found: " + khachHang.getTaiKhoan());
            
            // Kiểm tra mật khẩu
            System.out.println("🔐 Checking password...");
            String rawPassword = loginRequest.getPass();
            System.out.println("   Raw password length: " + (rawPassword != null ? rawPassword.length() : "NULL"));
            System.out.println("   Raw password (first 3 chars): " + (rawPassword != null && rawPassword.length() >= 3 ? rawPassword.substring(0, 3) + "..." : rawPassword));
            
            String dbPassword = khachHang.getMatKhau();
            System.out.println("   DB password: " + (dbPassword != null ? 
                (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") ? 
                    "BCrypt hash (length: " + dbPassword.length() + ")" : 
                    "Plain text (length: " + dbPassword.length() + ", value: " + dbPassword + ")") : "NULL"));
            
            // Kiểm tra xem password có phải BCrypt hash không
            boolean passwordMatches = false;
            
            if (dbPassword == null) {
                System.out.println("   ❌ DB password is NULL!");
                passwordMatches = false;
            } else if (rawPassword == null) {
                System.out.println("   ❌ Raw password is NULL!");
                passwordMatches = false;
            } else {
                // Nếu password là BCrypt hash (bắt đầu bằng $2a$ hoặc $2b$)
                if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$")) {
                    System.out.println("   Password type: BCrypt");
                    if (passwordService == null) {
                        System.err.println("   ❌ passwordService is NULL!");
                        passwordMatches = false;
                    } else {
                        try {
                            passwordMatches = passwordService.matches(rawPassword, dbPassword);
                            System.out.println("   BCrypt match result: " + passwordMatches);
                        } catch (Exception e) {
                            System.err.println("   ❌ Error in BCrypt matching: " + e.getMessage());
                            e.printStackTrace();
                            passwordMatches = false;
                        }
                    }
                } else {
                    // Nếu password là plain text (tạm thời hỗ trợ để migration)
                    System.out.println("   Password type: Plain text (legacy)");
                    // Trim và so sánh để tránh lỗi whitespace
                    passwordMatches = dbPassword.trim().equals(rawPassword.trim());
                    System.out.println("   Plain text match result: " + passwordMatches);
                    if (!passwordMatches) {
                        System.out.println("   DB password (trimmed): [" + dbPassword.trim() + "]");
                        System.out.println("   Raw password (trimmed): [" + rawPassword.trim() + "]");
                        System.out.println("   Length comparison - DB: " + dbPassword.trim().length() + ", Raw: " + rawPassword.trim().length());
                    }
                }
            }
            
            System.out.println("   Final password matches: " + passwordMatches);
            
            if (!passwordMatches) {
                System.out.println("❌ Wrong password for user: " + loginRequest.getUsername());
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "WRONG_PASSWORD");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            // Tạo response user
            UserLoginResponse user = UserLoginResponse.builder()
                .id(khachHang.getId())
                .hoTen(khachHang.getHoTen())
                .hinhAnh(khachHang.getHinhAnh())
                .ngaySinh(khachHang.getNgaySinh() != null ? khachHang.getNgaySinh().toString() : null)
                .ngayTao(khachHang.getNgayTao() != null ? khachHang.getNgayTao().toString() : null)
                .soDienThoai(khachHang.getSoDienThoai())
                .gioiTinh(khachHang.getGioiTinh())
                .email(khachHang.getEmail())
                .taiKhoan(khachHang.getTaiKhoan())
                .trangThai(khachHang.getTrangThai())
                .build();
            
            // Tạo JWT tokens
            String accessToken = jwtTokenProvider.generateToken(khachHang.getTaiKhoan(), "KHACH_HANG", user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(khachHang.getTaiKhoan());
            
            System.out.println("✅ Login successful for: " + loginRequest.getUsername());
            
            // Log successful login
            auditService.logSuccessfulLogin(loginRequest.getUsername(), clientIp, "KHACH_HANG");
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in authenticateKhachHang: " + e.getMessage());
            e.printStackTrace();
            auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
    public ResponseEntity<?> authenticateNhanVien(loginTest loginRequest, String clientIp) {
        try {
            System.out.println("🔍 [AUTH SERVICE] Authenticating NhanVien: " + loginRequest.getUsername());
            System.out.println("🔍 [AUTH SERVICE] Client IP: " + clientIp);
            
            // Tìm nhân viên theo tài khoản
            Optional<NhanVien> nhanVienOpt = nhanVienRepository
                .findByTaiKhoanAndTrangThai(loginRequest.getUsername(), 1);
            
            if (nhanVienOpt.isEmpty()) {
                System.out.println("❌ User not found: " + loginRequest.getUsername());
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            NhanVien nhanVien = nhanVienOpt.get();
            System.out.println("✅ User found: " + nhanVien.getTaiKhoan());
            
            // Kiểm tra mật khẩu
            System.out.println("🔐 Checking password...");
            System.out.println("   Raw password: " + loginRequest.getPass());
            
            // Kiểm tra xem password có phải BCrypt hash không
            boolean passwordMatches = false;
            String dbPassword = nhanVien.getMatKhau();
            
            if (dbPassword != null) {
                // Nếu password là BCrypt hash (bắt đầu bằng $2a$ hoặc $2b$)
                if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$")) {
                    passwordMatches = passwordService.matches(loginRequest.getPass(), dbPassword);
                    System.out.println("   Password type: BCrypt");
                } else {
                    // Nếu password là plain text (tạm thời hỗ trợ để migration)
                    passwordMatches = dbPassword.equals(loginRequest.getPass());
                    System.out.println("   Password type: Plain text (legacy)");
                }
            }
            
            System.out.println("   Password matches: " + passwordMatches);
            
            if (!passwordMatches) {
                System.out.println("❌ Wrong password for user: " + loginRequest.getUsername());
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "WRONG_PASSWORD");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            // Tạo response user
            UserLoginResponse user = UserLoginResponse.builder()
                .id(nhanVien.getId())
                .hoTen(nhanVien.getHoTen())
                .hinhAnh(nhanVien.getHinhAnh())
                .ngaySinh(nhanVien.getNgaySinh() != null ? nhanVien.getNgaySinh().toString() : null)
                .ngayTao(nhanVien.getNgayTao() != null ? nhanVien.getNgayTao().toString() : null)
                .soDienThoai(nhanVien.getSoDienThoai())
                .gioiTinh(nhanVien.getGioiTinh())
                .email(nhanVien.getEmail())
                .taiKhoan(nhanVien.getTaiKhoan())
                .chucVu(nhanVien.getChucVu() != null ? nhanVien.getChucVu().toMap() : null)
                .trangThai(nhanVien.getTrangThai())
                .build();
            
            // Tạo JWT tokens
            String role = nhanVien.getChucVu() != null ? nhanVien.getChucVu().getTen() : "NHAN_VIEN";
            String accessToken = jwtTokenProvider.generateToken(nhanVien.getTaiKhoan(), role, user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(nhanVien.getTaiKhoan());
            
            System.out.println("✅ Login successful for: " + loginRequest.getUsername());
            
            // Log successful login
            auditService.logSuccessfulLogin(loginRequest.getUsername(), clientIp, role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in authenticateNhanVien: " + e.getMessage());
            e.printStackTrace();
            auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
    public ResponseEntity<?> refreshToken(String refreshToken) {
        try {
            System.out.println("🔄 [AUTH SERVICE] Refreshing token...");
            
            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                System.out.println("❌ [AUTH SERVICE] Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token không hợp lệ"));
            }
            
            // Check if token is expired
            if (jwtTokenProvider.isTokenExpired(refreshToken)) {
                System.out.println("❌ [AUTH SERVICE] Refresh token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token đã hết hạn"));
            }
            
            // Get username from refresh token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            if (username == null) {
                System.out.println("❌ [AUTH SERVICE] Cannot extract username from refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token không hợp lệ"));
            }
            
            System.out.println("✅ [AUTH SERVICE] Username from token: " + username);
            
            // Tìm user - thử tìm trong KhachHang trước
            Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByTaiKhoanAndTrangThai(username, 1);
            
            UserLoginResponse user;
            String role;
            String newAccessToken;
            
            if (khachHangOpt.isPresent()) {
                // User là Khách hàng
                KhachHang khachHang = khachHangOpt.get();
                System.out.println("✅ [AUTH SERVICE] Found KhachHang: " + khachHang.getTaiKhoan());
                
                user = UserLoginResponse.builder()
                    .id(khachHang.getId())
                    .hoTen(khachHang.getHoTen())
                    .hinhAnh(khachHang.getHinhAnh())
                    .ngaySinh(khachHang.getNgaySinh() != null ? khachHang.getNgaySinh().toString() : null)
                    .ngayTao(khachHang.getNgayTao() != null ? khachHang.getNgayTao().toString() : null)
                    .soDienThoai(khachHang.getSoDienThoai())
                    .gioiTinh(khachHang.getGioiTinh())
                    .email(khachHang.getEmail())
                    .taiKhoan(khachHang.getTaiKhoan())
                    .trangThai(khachHang.getTrangThai())
                    .build();
                
                role = "KHACH_HANG";
                newAccessToken = jwtTokenProvider.generateToken(khachHang.getTaiKhoan(), role, user);
                
            } else {
                // Thử tìm trong NhanVien
                Optional<NhanVien> nhanVienOpt = nhanVienRepository
                    .findByTaiKhoanAndTrangThai(username, 1);
                
                if (nhanVienOpt.isPresent()) {
                    // User là Nhân viên
                    NhanVien nhanVien = nhanVienOpt.get();
                    System.out.println("✅ [AUTH SERVICE] Found NhanVien: " + nhanVien.getTaiKhoan());
                    
                    user = UserLoginResponse.builder()
                        .id(nhanVien.getId())
                        .hoTen(nhanVien.getHoTen())
                        .hinhAnh(nhanVien.getHinhAnh())
                        .ngaySinh(nhanVien.getNgaySinh() != null ? nhanVien.getNgaySinh().toString() : null)
                        .ngayTao(nhanVien.getNgayTao() != null ? nhanVien.getNgayTao().toString() : null)
                        .soDienThoai(nhanVien.getSoDienThoai())
                        .gioiTinh(nhanVien.getGioiTinh())
                        .email(nhanVien.getEmail())
                        .taiKhoan(nhanVien.getTaiKhoan())
                        .chucVu(nhanVien.getChucVu() != null ? nhanVien.getChucVu().toMap() : null)
                        .trangThai(nhanVien.getTrangThai())
                        .build();
                    
                    role = nhanVien.getChucVu() != null ? nhanVien.getChucVu().getTen() : "NHAN_VIEN";
                    newAccessToken = jwtTokenProvider.generateToken(nhanVien.getTaiKhoan(), role, user);
                    
                } else {
                    // Không tìm thấy user
                    System.out.println("❌ [AUTH SERVICE] User not found: " + username);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new erroResponse("Người dùng không tồn tại"));
                }
            }
            
            // Tạo response giống format login
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", refreshToken); // Giữ nguyên refreshToken
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            System.out.println("✅ [AUTH SERVICE] Token refreshed successfully for: " + username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in refreshToken: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi khi refresh token: " + e.getMessage()));
        }
    }
}

