package datn.be.mycode.service;

import datn.be.mycode.config.JwtTokenProvider;
import datn.be.mycode.entity.GioHang;
import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.entity.loginTest;
import datn.be.mycode.repository.GioHangRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.request.KhachHangRequest;
import datn.be.mycode.request.NormalTableRequestKhachHang;
import datn.be.mycode.request.khachHang.CapNhatEmailKhachHangRequest;
import datn.be.mycode.request.khachHang.CapNhatSoDienThoaiKhachHangRequest;
import datn.be.mycode.request.khachHang.UpdateKhachHangRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.UserLoginResponse;
import datn.be.mycode.response.erroResponse;
import datn.be.mycode.response.khachHang.ThongTinKhachHangResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KhachHangService {
    @Autowired
    private KhachHangRepository repository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private PasswordService passwordService;

    public NormalTableResponse<KhachHang> getAll(NormalTableRequestKhachHang request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }


        NormalTableResponse<KhachHang> khachHangNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<KhachHang> khachHangPage;
        if (request.getKeyWord() != null) {
            khachHangPage = repository.searchByTen(request.getKeyWord(), pageable);
            if(khachHangPage.getTotalElements() == 0){
                khachHangPage = repository.searchBySoDienThoai(request.getKeyWord(), pageable);
                if(khachHangPage.getTotalElements() == 0){
                    khachHangPage = repository.searchByTaiKhoan(request.getKeyWord(), pageable);
                    if(khachHangPage.getTotalElements() == 0){
                        khachHangPage = repository.searchByEmail(request.getKeyWord(), pageable);
                    }
                }
            }
        } else if (request.getStatus() != null){
            khachHangPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }else{
            khachHangPage = repository.findAll(pageable);
        }



        List<KhachHang> khachHangs = khachHangPage.getContent().stream().toList();

        khachHangNormalTableResponse.setItem(khachHangs);
        khachHangNormalTableResponse.setPage(request.getPage());
        khachHangNormalTableResponse.setPageSize(request.getPageSize());
        khachHangNormalTableResponse.setTotalItem(khachHangPage.getTotalElements());

        return khachHangNormalTableResponse;
    }

    public List<KhachHang> getAll() {

        return repository.findAll();
    }
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public KhachHang getById(Long id){
        return repository.findById(id).get();
    }


    public ThongTinKhachHangResponse getThongTinById(Long id) {
        KhachHang khachHang = repository.findById(id).get(); // Lấy trực tiếp khách hàng

        // Chuyển đổi sang KhachHangResponse
        return new ThongTinKhachHangResponse(
                khachHang.getId(),
                khachHang.getMa(),
                khachHang.getHoTen(),
                khachHang.getHinhAnh(),
                khachHang.getNgaySinh(),
                khachHang.getNgayTao(),
                khachHang.getSoDienThoai(),
                khachHang.getGioiTinh(),
                khachHang.getEmail(),
                khachHang.getTrangThai()
        );
    }


    public KhachHang add(KhachHangRequest request) {

        KhachHang khachHang = new KhachHang();
        khachHang.setMa("");
        khachHang.setHoTen(request.getHoTen());
        khachHang.setHinhAnh(request.getHinhAnh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(1);
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setEmail(request.getEmail());
        khachHang.setTaiKhoan(request.getTaiKhoan());
        khachHang.setMatKhau(request.getMatKhau());

        khachHang = repository.save(khachHang);
        khachHang.setMa("KH"+khachHang.getId());
        creatGioHang(khachHang);

        return repository.save(khachHang);
    }

    private void creatGioHang(KhachHang khachHang) {
        var setGia = BigDecimal.valueOf(0);
        GioHang gioHang = GioHang.builder()
                .ngayTao(LocalDateTime.now())
                .phiShip(setGia)
                .tongTien(setGia)
                .tongTienGiam(setGia)
                .tongTienGiam(setGia)
                .trangThai(1)
                .khachHang(khachHang)
                .build();
        gioHang = gioHangRepository.save(gioHang);
        gioHang.setMa("GH"+gioHang.getId());
        gioHangRepository.save(gioHang);
    }


    public KhachHang update(KhachHangRequest request) {
        KhachHang khachHang = repository.findById(request.getId()).get();
        khachHang.setMa(request.getMa());
        khachHang.setHoTen(request.getHoTen());
        khachHang.setHinhAnh(request.getHinhAnh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(request.getTrangThai());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setEmail(request.getEmail());
        khachHang.setTaiKhoan(request.getTaiKhoan());
        khachHang.setMatKhau(request.getMatKhau());
        return repository.save(khachHang);
    }
    public KhachHang updateByEmail(String email, String matKhau) {
        KhachHang khachHang = repository.findByEmail(email);

        khachHang.setMatKhau(matKhau);  khachHang.setEmail(email);
        return repository.save(khachHang);
    }
    public KhachHang findByEmail(String email) {
        return repository.findByEmail(email);
    }
    public KhachHang updateKhachHang(UpdateKhachHangRequest request) {
        KhachHang khachHang = repository.findById(request.getId()).get();
        khachHang.setMa(request.getMa());
        khachHang.setHoTen(request.getHoTen());
        khachHang.setHinhAnh(request.getHinhAnh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setGioiTinh(request.getGioiTinh());
        return repository.save(khachHang);
    }

//    public void delete(KhachHang khachHang) {
//        repository.delete(khachHang);
//    }
//
//
//    public KhachHang deleteById(Long id) {
//        KhachHang khachHang = repository.findById(id).get();
//        khachHang.setTrangThai(0);
//        return repository.save(khachHang);
//    }

    public KhachHang udateTrangThai(Long id, Integer status){
        KhachHang khachHang = repository.findById(id).get();
        khachHang.setTrangThai(status);
        return repository.save(khachHang);
    }
//    public KhachHang udateMatKhau(String email, String matKhau){
//        KhachHang khachHang = repository.findByEmail().get();
//        khachHang.setMatKhau(matKhau);
//        return repository.save(khachHang);
//    }

    public ResponseEntity<?> getAccount(loginTest loginRequest) {
        try {
            System.out.println("🔍 [KhachHangService] getAccount called for: " + loginRequest.getUsername());
            
            // Tìm khách hàng theo username (không so sánh password trong SQL)
            // Thử tìm với trangThai = 1 trước, nếu không tìm thấy thì tìm không cần trangThai
            Optional<KhachHang> khachHangOpt = repository.findByTaiKhoanAndTrangThai(loginRequest.getUsername(), 1);
            
            if (khachHangOpt.isEmpty()) {
                System.out.println("   ⚠️ User not found with trangThai=1, trying without trangThai check...");
                khachHangOpt = repository.findByTaiKhoan(loginRequest.getUsername());
                
                if (khachHangOpt.isPresent()) {
                    KhachHang found = khachHangOpt.get();
                    System.out.println("   ⚠️ User found but trangThai=" + found.getTrangThai() + " (expected: 1)");
                }
            }
            
            KhachHang khachHang = khachHangOpt.orElse(null);
            
            Map<String, String> response = new HashMap<>();

            if (khachHang == null) {
                System.out.println("❌ [KhachHangService] User not found: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            System.out.println("✅ [KhachHangService] User found: " + khachHang.getTaiKhoan() + ", trangThai: " + khachHang.getTrangThai());

            // Kiểm tra password - hỗ trợ cả BCrypt và plain text
            String dbPassword = khachHang.getMatKhau();
            String rawPassword = loginRequest.getPass();
            boolean passwordMatches = false;
            
            System.out.println("🔐 [KhachHangService] Checking password...");
            System.out.println("   Raw password length: " + (rawPassword != null ? rawPassword.length() : "NULL"));
            System.out.println("   DB password: " + (dbPassword != null ? 
                (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") ? 
                    "BCrypt hash" : "Plain text: " + dbPassword) : "NULL"));
            
            if (dbPassword == null || rawPassword == null) {
                System.out.println("   ❌ Password is NULL!");
                passwordMatches = false;
            } else {
                // Nếu password là BCrypt hash (bắt đầu bằng $2a$ hoặc $2b$)
                if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$")) {
                    System.out.println("   Password type: BCrypt");
                    // Dùng BCrypt matching
                    if (passwordService != null) {
                        try {
                            passwordMatches = passwordService.matches(rawPassword.trim(), dbPassword);
                            System.out.println("   BCrypt match result: " + passwordMatches);
                        } catch (Exception e) {
                            System.err.println("   ❌ Error in BCrypt matching: " + e.getMessage());
                            passwordMatches = false;
                        }
                    } else {
                        System.err.println("   ❌ passwordService is NULL!");
                        passwordMatches = false;
                    }
                } else {
                    // Nếu password là plain text (hỗ trợ legacy)
                    System.out.println("   Password type: Plain text (legacy)");
                    // Trim để tránh lỗi whitespace
                    passwordMatches = dbPassword.trim().equals(rawPassword.trim());
                    System.out.println("   Plain text match result: " + passwordMatches);
                    if (!passwordMatches) {
                        System.out.println("   DB password (trimmed): [" + dbPassword.trim() + "]");
                        System.out.println("   Raw password (trimmed): [" + rawPassword.trim() + "]");
                        System.out.println("   Length - DB: " + dbPassword.trim().length() + ", Raw: " + rawPassword.trim().length());
                    }
                }
            }

            if (!passwordMatches) {
                System.out.println("❌ [KhachHangService] Wrong password for user: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new erroResponse("Tài khoản hoặc mật khẩu không chính xác"));
            }
            
            System.out.println("✅ [KhachHangService] Password matches!");

            // Password đúng, tạo token
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
                    .matKhau(khachHang.getMatKhau())
                    .trangThai(khachHang.getTrangThai())
                    .build();

            String token = jwtTokenProvider.generateToken(khachHang.getTaiKhoan(), "KhachHang", user);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAccount: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new erroResponse("Lỗi hệ thống, vui lòng thử lại sau"));
        }
    }

    public KhachHang updateSoDienThoaiKhachHang(CapNhatSoDienThoaiKhachHangRequest request) {
        KhachHang khachHang = repository.findById(request.getId()).get();
        khachHang.setSoDienThoai(request.getSoDienThoai());
        return repository.save(khachHang);
    }
    public KhachHang updateEmailKhachHang(CapNhatEmailKhachHangRequest request) {
        KhachHang khachHang = repository.findById(request.getId()).get();
        khachHang.setEmail(request.getEmail());
        return repository.save(khachHang);
    }

}
