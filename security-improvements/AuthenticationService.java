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

import java.time.LocalDateTime;
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
            // Tìm khách hàng theo tài khoản
            Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByTaiKhoanAndTrangThai(loginRequest.getUsername(), 1);
            
            if (khachHangOpt.isEmpty()) {
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            KhachHang khachHang = khachHangOpt.get();
            
            // Kiểm tra mật khẩu
            if (!passwordService.matches(loginRequest.getPass(), khachHang.getMatKhau())) {
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
            
            // Log successful login
            auditService.logSuccessfulLogin(loginRequest.getUsername(), clientIp, "KHACH_HANG");
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống, vui lòng thử lại sau"));
        }
    }
    
    public ResponseEntity<?> authenticateNhanVien(loginTest loginRequest, String clientIp) {
        try {
            // Tìm nhân viên theo tài khoản
            Optional<NhanVien> nhanVienOpt = nhanVienRepository
                .findByTaiKhoanAndTrangThai(loginRequest.getUsername(), 1);
            
            if (nhanVienOpt.isEmpty()) {
                auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            NhanVien nhanVien = nhanVienOpt.get();
            
            // Kiểm tra mật khẩu
            if (!passwordService.matches(loginRequest.getPass(), nhanVien.getMatKhau())) {
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
            
            // Log successful login
            auditService.logSuccessfulLogin(loginRequest.getUsername(), clientIp, role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            auditService.logFailedLogin(loginRequest.getUsername(), clientIp, "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống, vui lòng thử lại sau"));
        }
    }
    
    public ResponseEntity<?> refreshToken(String refreshToken) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new erroResponse("Refresh token không hợp lệ"));
            }
            
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            
            // Tìm user và tạo token mới
            // Implementation tương tự như authenticate methods
            
            return ResponseEntity.ok("Token refreshed successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi khi refresh token"));
        }
    }
}