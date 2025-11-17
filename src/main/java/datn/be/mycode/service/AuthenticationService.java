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
            System.out.println("🔍 Authenticating KhachHang: " + loginRequest.getUsername());
            
            // Tìm khách hàng theo tài khoản
            Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByTaiKhoanAndTrangThai(loginRequest.getUsername(), 1);
            
            if (khachHangOpt.isEmpty()) {
                System.out.println("❌ User not found: " + loginRequest.getUsername());
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            KhachHang khachHang = khachHangOpt.get();
            System.out.println("✅ User found: " + khachHang.getTaiKhoan());
            
            // Kiểm tra mật khẩu
            System.out.println("🔐 Checking password...");
            System.out.println("   Raw password: " + loginRequest.getPass());
            System.out.println("   DB password: " + (khachHang.getMatKhau() != null ? 
                (khachHang.getMatKhau().startsWith("$2a$") || khachHang.getMatKhau().startsWith("$2b$") ? 
                    "BCrypt hash" : khachHang.getMatKhau()) : "NULL"));
            
            // Kiểm tra xem password có phải BCrypt hash không
            boolean passwordMatches = false;
            String dbPassword = khachHang.getMatKhau();
            
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
            System.out.println("🔍 Authenticating NhanVien: " + loginRequest.getUsername());
            
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
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token không hợp lệ"));
            }
            
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token không hợp lệ"));
            }
            
            // TODO: Tìm user và tạo token mới (cần implement đầy đủ)
            // Tạm thời trả về success
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token refreshed successfully");
            response.put("accessToken", refreshToken); // Tạm thời dùng refreshToken làm accessToken
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in refreshToken: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi khi refresh token: " + e.getMessage()));
        }
    }
}

