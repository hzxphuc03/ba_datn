package datn.be.mycode.config;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Data Initializer - Tạo dữ liệu ban đầu cho database
 * Chỉ chạy khi database trống (chưa có dữ liệu)
 */
@Component
@Profile("prod") // Chỉ chạy trong production profile
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ChucVuRepository chucVuRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichcoRepository kichcoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            logger.info("=== Bắt đầu khởi tạo dữ liệu ban đầu ===");

            // Kiểm tra xem đã có dữ liệu chưa
            // Sử dụng try-catch để xử lý trường hợp table chưa tồn tại
            long chucVuCount = 0;
            try {
                chucVuCount = chucVuRepository.count();
            } catch (Exception e) {
                logger.warn("Không thể kiểm tra số lượng chức vụ, có thể table chưa tồn tại: {}", e.getMessage());
                // Nếu table chưa tồn tại, tiếp tục tạo dữ liệu
            }

            if (chucVuCount > 0) {
                logger.info("Database đã có dữ liệu ({} chức vụ), bỏ qua việc khởi tạo dữ liệu mẫu", chucVuCount);
                return;
            }

            logger.info("Database trống, bắt đầu tạo dữ liệu mẫu...");

            // 1. Tạo Chức vụ
            createChucVu();

            // 2. Tạo Nhân viên Admin
            createAdminUser();

            // 3. Tạo Phương thức thanh toán
            createPhuongThucThanhToan();

            // 4. Tạo dữ liệu mẫu khác
            createSampleData();

            logger.info("=== Hoàn thành khởi tạo dữ liệu ban đầu ===");
        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo dữ liệu ban đầu: {}", e.getMessage(), e);
            // Không throw exception để ứng dụng vẫn có thể start
            // Dữ liệu có thể được tạo thủ công sau
        }
    }

    private void createChucVu() {
        logger.info("Tạo chức vụ...");

        // Kiểm tra xem chức vụ đã tồn tại chưa
        if (chucVuRepository.findByTen("ADMIN").isPresent()) {
            logger.info("Chức vụ ADMIN đã tồn tại, bỏ qua");
        } else {
            // Chức vụ ADMIN
            ChucVu admin = new ChucVu();
            admin.setTen("ADMIN");
            admin.setNgayTao(LocalDateTime.now());
            admin.setNgaySua(LocalDateTime.now());
            admin.setTrangThai(1);
            chucVuRepository.saveAndFlush(admin);
            logger.info("Đã tạo chức vụ: ADMIN");
        }

        if (chucVuRepository.findByTen("NHAN_VIEN").isPresent()) {
            logger.info("Chức vụ NHAN_VIEN đã tồn tại, bỏ qua");
        } else {
            // Chức vụ NHAN_VIEN
            ChucVu nhanVien = new ChucVu();
            nhanVien.setTen("NHAN_VIEN");
            nhanVien.setNgayTao(LocalDateTime.now());
            nhanVien.setNgaySua(LocalDateTime.now());
            nhanVien.setTrangThai(1);
            chucVuRepository.saveAndFlush(nhanVien);
            logger.info("Đã tạo chức vụ: NHAN_VIEN");
        }
    }

    private void createAdminUser() {
        logger.info("Tạo tài khoản Admin...");

        // Kiểm tra xem admin đã tồn tại chưa
        if (nhanVienRepository.findByTaiKhoanAndTrangThai("admin", 1).isPresent()) {
            logger.info("Tài khoản admin đã tồn tại, bỏ qua");
            return;
        }

        // Tìm chức vụ ADMIN
        ChucVu adminChucVu = chucVuRepository.findByTen("ADMIN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ ADMIN. Vui lòng tạo chức vụ trước."));

        // Tạo nhân viên Admin
        NhanVien admin = new NhanVien();
        admin.setMa("NV001");
        admin.setHoTen("Administrator");
        admin.setEmail("admin@datn.com");
        admin.setTaiKhoan("admin");
        // Hash password: "admin123" (BCrypt)
        admin.setMatKhau(passwordEncoder.encode("admin123"));
        admin.setSoDienThoai("0123456789");
        admin.setGioiTinh(1);
        admin.setDiaChi("Hà Nội");
        admin.setTrangThai(1);
        admin.setNgayTao(LocalDateTime.now());
        admin.setChucVu(adminChucVu);

        nhanVienRepository.save(admin);
        logger.info("Đã tạo tài khoản Admin:");
        logger.info("  - Username: admin");
        logger.info("  - Password: admin123");
        logger.info("  - Email: admin@datn.com");
    }

    private void createPhuongThucThanhToan() {
        logger.info("Tạo phương thức thanh toán...");

        // Kiểm tra xem đã có phương thức thanh toán chưa
        long count = phuongThucThanhToanRepository.count();
        if (count > 0) {
            logger.info("Đã có {} phương thức thanh toán, bỏ qua việc tạo mới", count);
            return;
        }

        // Thanh toán khi nhận hàng
        PhuongThucThanhToan cod = new PhuongThucThanhToan();
        cod.setTen("Thanh toán khi nhận hàng (COD)");
        cod.setNgayTao(LocalDateTime.now());
        cod.setNgaySua(LocalDateTime.now());
        cod.setTrangThai(1);
        phuongThucThanhToanRepository.save(cod);
        logger.info("Đã tạo phương thức: Thanh toán khi nhận hàng (COD)");

        // Thanh toán online (VNPay)
        PhuongThucThanhToan online = new PhuongThucThanhToan();
        online.setTen("Thanh toán online (VNPay)");
        online.setNgayTao(LocalDateTime.now());
        online.setNgaySua(LocalDateTime.now());
        online.setTrangThai(1);
        phuongThucThanhToanRepository.save(online);
        logger.info("Đã tạo phương thức: Thanh toán online (VNPay)");
    }

    private void createSampleData() {
        logger.info("Tạo dữ liệu mẫu...");

        // Tạo Thể loại mẫu
        if (theLoaiRepository.count() == 0) {
            TheLoai theLoai1 = new TheLoai();
            theLoai1.setTen("Giày thể thao");
            theLoai1.setNgayTao(LocalDateTime.now());
            theLoai1.setNgaySua(LocalDateTime.now());
            theLoai1.setTrangThai(1);
            theLoaiRepository.save(theLoai1);

            TheLoai theLoai2 = new TheLoai();
            theLoai2.setTen("Giày chạy bộ");
            theLoai2.setNgayTao(LocalDateTime.now());
            theLoai2.setNgaySua(LocalDateTime.now());
            theLoai2.setTrangThai(1);
            theLoaiRepository.save(theLoai2);

            TheLoai theLoai3 = new TheLoai();
            theLoai3.setTen("Giày da");
            theLoai3.setNgayTao(LocalDateTime.now());
            theLoai3.setNgaySua(LocalDateTime.now());
            theLoai3.setTrangThai(1);
            theLoaiRepository.save(theLoai3);
            logger.info("Đã tạo 3 thể loại mẫu");
        } else {
            logger.info("Đã có thể loại, bỏ qua việc tạo mới");
        }

        // Tạo Thương hiệu mẫu
        if (thuongHieuRepository.count() == 0) {
            ThuongHieu thuongHieu1 = new ThuongHieu();
            thuongHieu1.setTen("Nike");
            thuongHieu1.setNgayTao(LocalDateTime.now());
            thuongHieu1.setNgaySua(LocalDateTime.now());
            thuongHieu1.setTrangThai(1);
            thuongHieuRepository.save(thuongHieu1);

            ThuongHieu thuongHieu2 = new ThuongHieu();
            thuongHieu2.setTen("Adidas");
            thuongHieu2.setNgayTao(LocalDateTime.now());
            thuongHieu2.setNgaySua(LocalDateTime.now());
            thuongHieu2.setTrangThai(1);
            thuongHieuRepository.save(thuongHieu2);

            ThuongHieu thuongHieu3 = new ThuongHieu();
            thuongHieu3.setTen("Puma");
            thuongHieu3.setNgayTao(LocalDateTime.now());
            thuongHieu3.setNgaySua(LocalDateTime.now());
            thuongHieu3.setTrangThai(1);
            thuongHieuRepository.save(thuongHieu3);
            logger.info("Đã tạo 3 thương hiệu mẫu");
        } else {
            logger.info("Đã có thương hiệu, bỏ qua việc tạo mới");
        }

        // Tạo Màu sắc mẫu
        if (mauSacRepository.count() == 0) {
            MauSac mauSac1 = new MauSac();
            mauSac1.setTen("Đen");
            mauSac1.setMaMau("#000000");
            mauSac1.setNgayTao(LocalDateTime.now());
            mauSac1.setNgaySua(LocalDateTime.now());
            mauSac1.setTrangThai(1);
            mauSacRepository.save(mauSac1);

            MauSac mauSac2 = new MauSac();
            mauSac2.setTen("Trắng");
            mauSac2.setMaMau("#FFFFFF");
            mauSac2.setNgayTao(LocalDateTime.now());
            mauSac2.setNgaySua(LocalDateTime.now());
            mauSac2.setTrangThai(1);
            mauSacRepository.save(mauSac2);

            MauSac mauSac3 = new MauSac();
            mauSac3.setTen("Đỏ");
            mauSac3.setMaMau("#FF0000");
            mauSac3.setNgayTao(LocalDateTime.now());
            mauSac3.setNgaySua(LocalDateTime.now());
            mauSac3.setTrangThai(1);
            mauSacRepository.save(mauSac3);

            MauSac mauSac4 = new MauSac();
            mauSac4.setTen("Xanh dương");
            mauSac4.setMaMau("#0000FF");
            mauSac4.setNgayTao(LocalDateTime.now());
            mauSac4.setNgaySua(LocalDateTime.now());
            mauSac4.setTrangThai(1);
            mauSacRepository.save(mauSac4);
            logger.info("Đã tạo 4 màu sắc mẫu");
        } else {
            logger.info("Đã có màu sắc, bỏ qua việc tạo mới");
        }

        // Tạo Kích cỡ mẫu
        if (kichcoRepository.count() == 0) {
            String[] kichCos = {"36", "37", "38", "39", "40", "41", "42", "43"};
            for (String kichCoTen : kichCos) {
                KichCo kichCo = new KichCo();
                kichCo.setTen(kichCoTen);
                kichCo.setNgayTao(LocalDateTime.now());
                kichCo.setNgaySua(LocalDateTime.now());
                kichCo.setTrangThai(1);
                kichcoRepository.save(kichCo);
            }
            logger.info("Đã tạo 8 kích cỡ mẫu (36-43)");
        } else {
            logger.info("Đã có kích cỡ, bỏ qua việc tạo mới");
        }
    }
}

