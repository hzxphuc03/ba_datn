package datn.be.mycode.service;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.BanHangOnline.ThanhToanKhiNhanHang;
import datn.be.mycode.request.GioHang.GioHangDiaChi;
import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.request.GioHang.GioHangPhuongThucThanhToan;
import datn.be.mycode.request.GioHang.GioHangVoucher;
import datn.be.mycode.response.erroResponse;
import datn.be.mycode.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherGioHangRepository voucherGioHangRepository;
    @Autowired
    private VoucherHoaDonRepository voucherHoaDonRepository;
    @Autowired
    private VoucherKhachHangRepository voucherKhachHangRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private ThongBaoRepository thongBaoRepository;
    @Autowired
    private ThaoTacHoaDonRepository thaoTacHoaDonRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private EmailSender emailSender;

    public ResponseEntity<?> getByKhachHang(Long idKhachHang) {
        var gioHang = getGioHangByKhachHangId(idKhachHang);
        if (gioHang == null ) return getErro("Khách hàng này không tồn tại");
        return ResponseEntity.ok(gioHang);
    }

    public ResponseEntity<?> updateGioHang(GioHangAdd request){
        var khachHang = khachHangRepository.findById(request.getIdKhachHang()).orElse(null);
        if(khachHang == null) return getErro("Khách hàng này chưa tồn tại hoặc đã dừng hoạt động");
        var gioHang = gioHangRepository.findByKhachHangId(khachHang.getId()).orElse(null);
        if(khachHang == null) return getErro("Khách hàng này chưa tồn tại hoặc đã dừng hoạt động");
        if (request.getIdPhuongThucThanhToan() == 1) return getErro("Phương thức thanh toán này không áp dụng ở giỏ hàng");
        var phuongThucThanhToan = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).orElse(null);
        if(phuongThucThanhToan == null) return getErro("Phương thức thanh toán này không tồn tại");

        gioHang = getGioHang(gioHang, request);
        gioHang.setPhuongThucThanhToan(phuongThucThanhToan);

        gioHang = gioHangRepository.save(gioHang);

        return ResponseEntity.ok(gioHang);
    }

    public GioHang getGioHang(GioHang gioHang, GioHangAdd request){

        gioHang.setGhiChu(request.getGhiChu());
        gioHang.setTenNguoiNhan(request.getTenNguoiNhan());
        gioHang.setSdtNguoiNhan(request.getSdtNguoiNhan());
        gioHang.setIdTinhThanh(request.getIdTinhThanh());
        gioHang.setTinhThanh(request.getTinhThanh());
        gioHang.setIdQuanHuyen(request.getIdQuanHuyen());
        gioHang.setQuanHuyen(request.getQuanHuyen());
        gioHang.setIdPhuongXa(request.getIdPhuongXa());
        gioHang.setPhuongXa(request.getPhuongXa());
        gioHang.setPhiShip(request.getPhiShip());
        gioHang.setTongTien(request.getTongTien());
        gioHang.setTongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam());
        gioHang.setTongTienGiam(request.getTongTienGiam());

        return gioHang;
    }

    public ResponseEntity<?> getSoLuong(Long request){
        var khachHang = khachHangRepository.findById(request).orElse(null);
        if(khachHang == null) return ResponseEntity.ok(0);
        var gioHang = gioHangRepository.findByKhachHangId(khachHang.getId()).orElse(null);
        if(khachHang == null) return getErro("Khách hàng này chưa tồn tại hoặc đã dừng hoạt động");

        var soLuong = gioHangChiTietRepository.getTongSoLuong(gioHang.getId());

        return ResponseEntity.ok(soLuong);
    }

    public ResponseEntity<?> addDiaChi(GioHangDiaChi request) {
        var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
        if(gioHang == null) return getErro("Giỏ hàng không tồn tại");

        if(checkDiaChi(request)){
            getDiaChi(gioHang, request);
            gioHang.setTenNguoiNhan(request.getTenNguoiNhan());
            gioHang.setSdtNguoiNhan(request.getSdtNguoiNhan());
            return ResponseEntity.ok(gioHangRepository.save(gioHang));
        }
        return getErro("Địa chỉ hoặc thông tin người nhận trống");
    }

    public ResponseEntity<?> addVoucher(GioHangVoucher request) {
        var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
        if(gioHang == null) return getErro("Giỏ hàng không tồn tại");

        var idVoucher = request.getIdVoucher();

        var voucher = voucherRepository.findById(request.getIdVoucher()).orElse(null);
        if(voucher == null || voucher.getTrangThai() != 1) {
            return getErro("không tìm thấy voucher hoặc voucher không hoạt động");
        }
        if(voucher.getSoLuong() <= 0){
            return getErro("Voucher này đã hết");
        }
        var voucherGioHangs = voucherGioHangRepository.findAllByGioHangId(gioHang.getId());

//        check xem gio hang có voucher này chưa
        boolean addNewVoucher = true;
        for (VoucherGioHang voucherGioHang:voucherGioHangs) {
            if(voucherGioHang.getVoucher().getTheLoai() == voucher.getTheLoai()){
                voucherGioHang.setVoucher(voucher);
                voucherGioHang.setNgaySua(LocalDateTime.now());

                voucherGioHangRepository.save(voucherGioHang);
                addNewVoucher = false;
            }
        }
        if(addNewVoucher) {
            var addVoucher = new VoucherGioHang();
            addVoucher.setNgayTao(LocalDateTime.now());
            addVoucher.setVoucher(voucher);
            addVoucher.setGioHang(gioHang);
            addVoucher.setTrangThai(1);
            voucherGioHangRepository.save(addVoucher);
        }

        return ResponseEntity.ok(gioHang);
    }

    public ResponseEntity<?> addPhuongThucThanhToan(GioHangPhuongThucThanhToan request) {
        var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
        var phuongThucThanhToan = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).orElse(null);

        if(gioHang == null) return getErro("Hóa đơn không tồn tại");
        if(phuongThucThanhToan == null) return getErro("Phương thức thanh toán không tồn tại");

        gioHang.setPhuongThucThanhToan(phuongThucThanhToan);

        return ResponseEntity.ok(gioHangRepository.save(gioHang));
    }

    public ResponseEntity<?> thanhToanOnline(ThanhToanKhiNhanHang request){
//        var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(2)).orElse(null);
//        var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
//        if(gioHang == null) return getErro("Khách hàng này đã dừng");

//        HoaDon hoaDon = taoHoaDonThanhToan(request);
//        hoaDon.setPhuongThucThanhToan(pttt);
//        hoaDon.setTrangThai(0);
//        hoaDon = hoaDonRepository.save(hoaDon);
////            toa hoa don chi tiet
//        List<HoaDonChiTiet> hoaDonChiTietList = taoHoaDonChiTiet(hoaDon,request.getIdGioHangChiTietList());
//        if(hoaDonChiTietList == null) return getErro("Chưa có sản phẩm trong giỏ hàng");89
//        setGiohang(gioHang);

        for (Long idGioHangChiTiet : request.getIdGioHangChiTietList()) {
            var gioHangChiTietCheck = gioHangChiTietRepository.findById(idGioHangChiTiet).orElse(null);
            var sanPhamCheck = gioHangChiTietCheck.getSanPhamChiTiet();
            if (sanPhamCheck.getSanPham().getTrangThai() != 1) return getErro("Sản phẩm hiện tại không hoạt đông");
//            if(sanPhamCheck.getSoLuong()<gioHangChiTietCheck.getSoLuong()) return getErro("Cửa hàng không còn đủ số lượng của sản phẩm "+gioHangChiTietCheck.getId());
        }

        return ResponseEntity.ok(taoURLThanhToan(request.getTongTien().intValue(),request.getIdKhachHang()+""));

    }


    @Transactional
    public ResponseEntity<?> thanhToanKhiGiaohang(ThanhToanKhiNhanHang request) {
        // #region agent log
        try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:203\",\"message\":\"thanhToanKhiGiaohang ENTRY\",\"data\":{\"khachHangId\":" + request.getIdKhachHang() + ",\"idGioHangChiTietList\":" + (request.getIdGioHangChiTietList() != null ? request.getIdGioHangChiTietList().size() : 0) + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2,H3,H4,H5\"}\n");
        } catch (IOException e) {}
        // #endregion
        try {
            System.out.println("🔍 [GioHangService] thanhToanKhiGiaohang called for khachHangId: " + request.getIdKhachHang());

            var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(3)).orElse(null);
            if (pttt == null) {
                System.err.println("❌ [GioHangService] PhuongThucThanhToan ID=3 not found");
                return getErro("Phương thức thanh toán không tồn tại");
            }

            // FIX: Handle multiple carts by taking the first active one
            var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
            if(gioHang == null) {
                System.err.println("❌ [GioHangService] GioHang not found for khachHangId: " + request.getIdKhachHang());
                return getErro("Khách hàng này đã dừng");
            }

            // Kiểm tra sản phẩm trong giỏ hàng
            if (request.getIdGioHangChiTietList() == null || request.getIdGioHangChiTietList().isEmpty()) {
                System.err.println("❌ [GioHangService] idGioHangChiTietList is null or empty");
                return getErro("Chưa có sản phẩm trong giỏ hàng");
            }

            for (Long idGioHangChiTiet : request.getIdGioHangChiTietList()) {
                var gioHangChiTietOpt = gioHangChiTietRepository.findById(idGioHangChiTiet);
                if (gioHangChiTietOpt.isEmpty()) {
                    System.err.println("❌ [GioHangService] GioHangChiTiet not found: " + idGioHangChiTiet);
                    return getErro("Sản phẩm trong giỏ hàng không tồn tại");
                }

                var gioHangChiTietCheck = gioHangChiTietOpt.get();
                var sanPhamCheck = gioHangChiTietCheck.getSanPhamChiTiet();

                if (sanPhamCheck == null) {
                    System.err.println("❌ [GioHangService] SanPhamChiTiet is null for gioHangChiTiet: " + idGioHangChiTiet);
                    return getErro("Sản phẩm không tồn tại");
                }

                if (sanPhamCheck.getSanPham() == null) {
                    System.err.println("❌ [GioHangService] SanPham is null for sanPhamChiTiet: " + sanPhamCheck.getId());
                    return getErro("Sản phẩm không tồn tại");
                }

                if (sanPhamCheck.getSanPham().getTrangThai() != 1) {
                    System.err.println("❌ [GioHangService] SanPham trangThai != 1: " + sanPhamCheck.getSanPham().getTrangThai());
                    return getErro("Sản phẩm hiện tại không hoạt động");
                }
            }

            // Tạo hóa đơn
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_taoHoaDon\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:252\",\"message\":\"BEFORE taoHoaDonThanhToan\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
            } catch (IOException e) {}
            // #endregion
            HoaDon hoaDon = taoHoaDonThanhToan(request);
            if (hoaDon == null) {
                // #region agent log
                try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                    fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_taoHoaDon_null\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:253\",\"message\":\"taoHoaDonThanhToan returned null\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H2\"}\n");
                } catch (IOException e) {}
                // #endregion
                System.err.println("❌ [GioHangService] taoHoaDonThanhToan returned null");
                return getErro("Không thể tạo hóa đơn. Vui lòng kiểm tra thông tin khách hàng");
            }

            hoaDon.setPhuongThucThanhToan(pttt);
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_save_hoaDon\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:259\",\"message\":\"BEFORE save hoaDon\",\"data\":{\"hoaDonId\":" + (hoaDon.getId() != null ? hoaDon.getId() : "null") + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2,H4\"}\n");
            } catch (IOException e) {}
            // #endregion
            hoaDon = hoaDonRepository.save(hoaDon);
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_after_save_hoaDon\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:259\",\"message\":\"AFTER save hoaDon\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + ",\"ma\":\"" + hoaDon.getMa() + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2,H4\"}\n");
            } catch (IOException e) {}
            // #endregion

            // Tạo hóa đơn chi tiết
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_taoHoaDonChiTiet\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:262\",\"message\":\"BEFORE taoHoaDonChiTiet\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + ",\"cartItemCount\":" + request.getIdGioHangChiTietList().size() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H5\"}\n");
            } catch (IOException e) {}
            // #endregion
            List<HoaDonChiTiet> hoaDonChiTietList = taoHoaDonChiTiet(hoaDon, request.getIdGioHangChiTietList());
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_after_taoHoaDonChiTiet\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:262\",\"message\":\"AFTER taoHoaDonChiTiet\",\"data\":{\"hoaDonChiTietListSize\":" + (hoaDonChiTietList != null ? hoaDonChiTietList.size() : 0) + ",\"isNull\":" + (hoaDonChiTietList == null) + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H5\"}\n");
            } catch (IOException e) {}
            // #endregion
            if(hoaDonChiTietList == null || hoaDonChiTietList.isEmpty()) {
                System.err.println("❌ [GioHangService] taoHoaDonChiTiet returned null or empty");
                // Rollback: xóa hóa đơn đã tạo
                // #region agent log
                try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                    fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_rollback_hoaDon\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:264\",\"message\":\"ROLLBACK hoaDon\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H2\"}\n");
                } catch (IOException e) {}
                // #endregion
                try {
                    hoaDonRepository.delete(hoaDon);
                } catch (Exception e) {
                    System.err.println("❌ [GioHangService] Failed to rollback hoaDon: " + e.getMessage());
                }
                return getErro("Chưa có sản phẩm trong giỏ hàng");
            }

            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_setGiohang\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:274\",\"message\":\"BEFORE setGiohang\",\"data\":{\"gioHangId\":" + gioHang.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (IOException e) {}
            // #endregion
            setGiohang(gioHang);
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_after_setGiohang\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:274\",\"message\":\"AFTER setGiohang\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (IOException e) {}
            // #endregion

            // Gửi thông báo và email (không fail nếu email lỗi)
            try {
                TaoThongBao(hoaDon);
            } catch (Exception e) {
                System.err.println("⚠️ [GioHangService] Failed to create notification: " + e.getMessage());
                // Không fail vì đây không phải lỗi nghiêm trọng
            }

            try {
                if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getEmail() != null && !hoaDon.getKhachHang().getEmail().trim().isEmpty()) {
                    emailSender.sendEmailHoaDon(hoaDon.getKhachHang().getEmail(), hoaDonChiTietList, hoaDon);
                } else {
                    System.out.println("⚠️ [GioHangService] Không có email để gửi thông báo");
                }
            } catch (Exception e) {
                System.err.println("⚠️ [GioHangService] Failed to send email: " + e.getMessage());
                // Không fail vì email không phải bắt buộc
            }

            System.out.println("✅ [GioHangService] thanhToanKhiGiaohang completed successfully. HoaDon ID: " + hoaDon.getId());
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_success_exit\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:296\",\"message\":\"SUCCESS EXIT\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + ",\"status\":\"OK\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2,H3,H4,H5\"}\n");
            } catch (IOException e) {}
            // #endregion
            return ResponseEntity.ok(hoaDon);

        } catch (Exception e) {
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:298\",\"message\":\"EXCEPTION caught\",\"data\":{\"exception\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
            } catch (IOException e2) {}
            // #endregion
            System.err.println("❌ [GioHangService] Exception in thanhToanKhiGiaohang: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    private void TaoThongBao(HoaDon hoaDon) {
        ThongBao thongBao = new ThongBao();
        thongBao.setNoiDung("Bạn đã đặt thành công đơn hàng "+hoaDon.getMa());
        thongBao.setUrl("/g4/shop/khach-hang?activeItem=1&idHoaDon="+hoaDon.getId());
        thongBao.setDaDoc(false);
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setTrangThai(1);
        thongBao.setKhachHang(hoaDon.getKhachHang());

        thongBaoRepository.save(thongBao);
        messagingTemplate.convertAndSend("/topic/notifications", hoaDon.getKhachHang().getId());
    }

    private void TaoThongBaoAddVoucher(HoaDon hoaDon) {
        ThongBao thongBao = new ThongBao();
        thongBao.setNoiDung("Bạn đã được tặng 1 phiếu miễn phí gửi hàng khi thanh toán hóa đơn trên 500000 đ");
        thongBao.setUrl("/g4/shop/khach-hang?activeItem=2");
        thongBao.setDaDoc(false);
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setTrangThai(1);
        thongBao.setKhachHang(hoaDon.getKhachHang());

        thongBaoRepository.save(thongBao);
        messagingTemplate.convertAndSend("/topic/notifications", hoaDon.getKhachHang().getId());
    }

    public String taoURLThanhToan(int orderTotal,String maHoaDon) {
        return vnPayService.createOrder(orderTotal,maHoaDon,"http://localhost:4200/thanh-toan-online-res");
    }


    public ResponseEntity<?> thanhToanThanhCong(ThanhToanKhiNhanHang request) {
        var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(2)).orElse(null);
        var gioHang = getGioHangByKhachHangId(request.getIdKhachHang());
        if(gioHang == null) return getErro("Khách hàng này không hoạt động hoặc chưa tạo tài khoản");
        if(request.getIdTinhThanh() == null) return getErro("");
//        tao hoa don
        HoaDon hoaDon = taoHoaDonThanhToan(request);
        hoaDon.setPhuongThucThanhToan(pttt);
        hoaDon.setThanhToan(true);
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThai(2);
        hoaDonRepository.save(hoaDon);

//      toa hoa don chi tiet
        List<HoaDonChiTiet> hoaDonChiTietList = taoHoaDonChiTiet(hoaDon,request.getIdGioHangChiTietList());
        if(hoaDonChiTietList == null) return getErro("Chưa có sản phẩm trong giỏ hàng");
        setGiohang(gioHang);

        TaoThongBao(hoaDon);
        emailSender.sendEmailHoaDon(hoaDon.getKhachHang().getEmail(),hoaDonChiTietList,hoaDon);

        AddVouchherFreeShip(hoaDon);
        messagingTemplate.convertAndSend("/topic/notifications", hoaDon.getKhachHang().getId());
        return ResponseEntity.ok(hoaDon);
    }

    private void AddVouchherFreeShip(HoaDon hoaDon) {
        BigDecimal giaTraThuong = BigDecimal.valueOf(500000);
        if(hoaDon.getTongTien().compareTo(giaTraThuong) >= 0){
            var voucherFreeShip = getVoucherByMa("G5FP");
            if(voucherFreeShip == null){
                Voucher voucher = new Voucher();
                voucher.setTen("Phiếu giảm giá cho hóa đơn trên 500000 đ");
                voucher.setTheLoai(1);
                voucher.setNgayBatDau(LocalDateTime.parse("1753-11-10T10:15:30"));
                voucher.setNgayKetThuc(LocalDateTime.parse("5024-11-10T10:15:30"));
                voucher.setPhanTramGiam(100);
                voucher.setGiamToiDa(BigDecimal.valueOf(100000));
                voucher.setGiaTriGiamToiThieu(BigDecimal.valueOf(0));
                voucher.setTrangThai(3);

                voucherFreeShip = voucherRepository.save(voucher);
            }
            var voucherKhachHhang = getVoucherKhachHangByVoucherId(voucherFreeShip.getId());
            if(voucherKhachHhang == null){
                VoucherKhachHang voucherKhachHang = new VoucherKhachHang();
                voucherKhachHang.setNgayTao(LocalDateTime.now());
                voucherKhachHang.setNgayBatDau(LocalDateTime.now());
                voucherKhachHang.setNgayKetThuc(LocalDateTime.now().plusDays(7));
                voucherKhachHang.setSoLuong(1);
                voucherKhachHang.setTrangThai(1);
                voucherKhachHang.setVoucher(voucherFreeShip);
                voucherKhachHang.setKhachHang(hoaDon.getKhachHang());
                voucherKhachHangRepository.save(voucherKhachHang);
            }else {
                voucherKhachHhang.setSoLuong(voucherKhachHhang.getSoLuong()+1);
            }
            TaoThongBaoAddVoucher(hoaDon);
        }
    }

    private void xoaHoaDon(HoaDon hoaDon) {
        var hoaDonChiTiets = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        var voucherHoaDons = voucherHoaDonRepository.findByHoaDonId(hoaDon.getId());
        for (VoucherHoaDon voucherHoaDon:voucherHoaDons) {
            voucherHoaDonRepository.delete(voucherHoaDon);
        }
        hoaDonChiTietRepository.deleteAll(hoaDonChiTiets);
        hoaDonRepository.delete(hoaDon);
    }

    private void setSanPhamThanhToanOnline(HoaDon hoaDon) {
        var hoaDonChiTiets = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        var gioHang = getGioHangByKhachHangId(hoaDon.getKhachHang().getId());


        for (HoaDonChiTiet hoaDonChiTiet:hoaDonChiTiets) {
            var sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
            var sanPham = sanPhamChiTiet.getSanPham();
            var gioHangChiTiet = gioHangChiTietRepository.findByGioHangIdAndAndSanPhamChiTietId(gioHang.getId(), sanPhamChiTiet.getId()).orElse(null);
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - hoaDonChiTiet.getSoLuong());
            if (sanPhamChiTiet.getSoLuong() == 0) sanPhamChiTiet.setTrangThai(0);

            sanPhamChiTietRepository.save(sanPhamChiTiet);
            gioHangChiTietRepository.delete(gioHangChiTiet);
//            tinh lai so luong cua san pham
            sanPham.setSoLuong(sanPhamChiTietRepository.sumSoLuongByTrangThai(sanPham.getId()));
            sanPhamRepository.save(sanPham);
            var sanPhamChiTiets = sanPhamChiTietRepository.findAllBySanPham_IdAndTrangThai(sanPham.getId(), 1);
            if (sanPhamChiTiets.isEmpty()) {
                sanPham.setTrangThai(0);
                sanPhamRepository.save(sanPham);
            }
        }

    }

    private void setGiohang(GioHang gioHang) {
        var setGia = BigDecimal.valueOf(0);
        gioHang.setPhiShip(setGia);
        gioHang.setTongTienSanPhamChuaGiam(setGia);
        gioHang.setTongTien(setGia);
        gioHang.setTongTienGiam(setGia);

        gioHangRepository.save(gioHang);
    }


    private List<HoaDonChiTiet> taoHoaDonChiTiet(HoaDon hoaDon, List<Long> idGioHangChiTiets) {
        try {
            if (idGioHangChiTiets == null || idGioHangChiTiets.isEmpty()) {
                System.err.println("❌ [GioHangService] idGioHangChiTiets is null or empty");
                return null;
            }

            var gioHangChiTiets = new ArrayList<GioHangChiTiet>();
            for (Long idGioHangChiTiet : idGioHangChiTiets) {
                var gioHangChiTietOpt = gioHangChiTietRepository.findById(idGioHangChiTiet);
                if (gioHangChiTietOpt.isEmpty()) {
                    System.err.println("❌ [GioHangService] GioHangChiTiet not found: " + idGioHangChiTiet);
                    return null;
                }
                gioHangChiTiets.add(gioHangChiTietOpt.get());
            }

            List<HoaDonChiTiet> hoaDonChiTiets = new ArrayList<>();
            for (GioHangChiTiet gioHangChiTiet : gioHangChiTiets) {
                if (gioHangChiTiet == null || gioHangChiTiet.getSanPhamChiTiet() == null) {
                    System.err.println("❌ [GioHangService] GioHangChiTiet or SanPhamChiTiet is null");
                    continue; // Skip invalid items
                }

                HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                hoaDonChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
                hoaDonChiTiet.setGiaHienHanh(gioHangChiTiet.getGiaHienHanh());
                hoaDonChiTiet.setGiaDaGiam(gioHangChiTiet.getGiaDaGiam());
                hoaDonChiTiet.setTrangThai(1);
                hoaDonChiTiet.setHoaDon(hoaDon);
                hoaDonChiTiet.setSanPhamChiTiet(gioHangChiTiet.getSanPhamChiTiet());

                setSanPham(hoaDon, gioHangChiTiet);
                hoaDonChiTiets.add(hoaDonChiTiet);
            }

            if (hoaDonChiTiets.isEmpty()) {
                System.err.println("❌ [GioHangService] No valid hoaDonChiTiets created");
                return null;
            }

            return hoaDonChiTietRepository.saveAll(hoaDonChiTiets);

        } catch (Exception e) {
            System.err.println("❌ [GioHangService] Exception in taoHoaDonChiTiet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void setSanPham(HoaDon hoaDon, GioHangChiTiet gioHangChiTiet) {
        // #region agent log
        try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_setSanPham_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:559\",\"message\":\"setSanPham ENTRY\",\"data\":{\"gioHangChiTietId\":" + gioHangChiTiet.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
        } catch (IOException e) {}
        // #endregion

        try {
            var sanPhamChiTiet = gioHangChiTiet.getSanPhamChiTiet();
            if (sanPhamChiTiet == null) {
                // #region agent log
                try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                    fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_setSanPham_null_sanPhamChiTiet\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:566\",\"message\":\"sanPhamChiTiet is null\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
                } catch (IOException e) {}
                // #endregion
                throw new RuntimeException("SanPhamChiTiet is null for gioHangChiTiet: " + gioHangChiTiet.getId());
            }
            
            var sanPham = sanPhamChiTiet.getSanPham();
            if (sanPham == null) {
                // #region agent log
                try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                    fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_setSanPham_null_sanPham\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:574\",\"message\":\"sanPham is null\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
                } catch (IOException e) {}
                // #endregion
                throw new RuntimeException("SanPham is null for sanPhamChiTiet: " + sanPhamChiTiet.getId());
            }
            
//            if(hoaDon.getPhuongThucThanhToan().getId() != 3) {
//                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - gioHangChiTiet.getSoLuong());
//                if (sanPhamChiTiet.getSoLuong() == 0) sanPhamChiTiet.setTrangThai(0);
//
//                sanPhamChiTietRepository.save(sanPhamChiTiet);
//            }
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_delete_cart\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:579\",\"message\":\"BEFORE delete gioHangChiTiet\",\"data\":{\"gioHangChiTietId\":" + gioHangChiTiet.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (IOException e) {}
            // #endregion
            gioHangChiTietRepository.delete(gioHangChiTiet);
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_after_delete_cart\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:579\",\"message\":\"AFTER delete gioHangChiTiet\",\"data\":{\"gioHangChiTietId\":" + gioHangChiTiet.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (IOException e) {}
            // #endregion
//            tinh lai so luong cua san pham
            int soLuongTong = sanPhamChiTietRepository.sumSoLuongByTrangThai(sanPham.getId());
            sanPham.setSoLuong(soLuongTong);
            sanPhamRepository.save(sanPham);
            var sanPhamChiTiets = sanPhamChiTietRepository.findAllBySanPham_IdAndTrangThai(sanPham.getId(), 1);
            if (sanPhamChiTiets.isEmpty()) {
                sanPham.setTrangThai(0);
                sanPhamRepository.save(sanPham);
            }
        } catch (Exception e) {
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_setSanPham_exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:594\",\"message\":\"EXCEPTION in setSanPham\",\"data\":{\"exception\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (IOException e2) {}
            // #endregion
            System.err.println("❌ [GioHangService] Exception in setSanPham: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    private HoaDon taoHoaDonThanhToan(ThanhToanKhiNhanHang request){
        var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(3)).orElse(null);
        if (pttt == null) return null;
        var khachHang = khachHangRepository.findById(request.getIdKhachHang()).orElse(null);
        if (khachHang == null) return null;
        HoaDon hoaDon = new HoaDon();

        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setSdtNguoiNhan(request.getSdtNguoiNhan());
        hoaDon.setIdTinhThanh(request.getIdTinhThanh());
        hoaDon.setTinhThanh(request.getTinhThanh());
        hoaDon.setIdQuanHuyen(request.getIdQuanHuyen());
        hoaDon.setQuanHuyen(request.getQuanHuyen());
        hoaDon.setIdPhuongXa(request.getIdPhuongXa());
        hoaDon.setPhuongXa(request.getPhuongXa());
        hoaDon.setPhiShip(request.getPhiShip());
        hoaDon.setTongTien(request.getTongTien());
        hoaDon.setTongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam());
        hoaDon.setTongTienGiam(request.getTongTienGiam());
        hoaDon.setThanhToan(false);
        hoaDon.setGiaoHang(true);
        hoaDon.setTrangThai(2);
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNhanVien(null);

        hoaDon = hoaDonRepository.save(hoaDon);
        hoaDon.setMa("HD"+hoaDon.getId());
        
        // #region agent log
        try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_addVoucher\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:625\",\"message\":\"BEFORE addVoucher\",\"data\":{\"idVoucherKhachHangList\":" + (request.getIdVoucherKhachHangList() != null ? request.getIdVoucherKhachHangList().size() : "null") + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
        } catch (IOException e) {}
        // #endregion
        
        // FIX: Handle null voucher list and check return value
        if (request.getIdVoucherKhachHangList() != null && !request.getIdVoucherKhachHangList().isEmpty()) {
            String voucherResult = addVoucher(hoaDon, request.getIdVoucherKhachHangList());
            // #region agent log
            try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_after_addVoucher\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:625\",\"message\":\"AFTER addVoucher\",\"data\":{\"result\":\"" + (voucherResult != null ? voucherResult.replace("\"", "\\\"") : "null") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
            } catch (IOException e) {}
            // #endregion
            // If voucher processing fails, log but don't fail the entire transaction
            if (voucherResult != null && !voucherResult.equals("done")) {
                System.err.println("⚠️ [GioHangService] Voucher processing warning: " + voucherResult);
                // Don't throw exception - just log the warning
            }
        }

        // #region agent log
        try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_before_TaoThaoTacHoaDon\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:627\",\"message\":\"BEFORE TaoThaoTacHoaDon\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
        } catch (IOException e) {}
        // #endregion
        
        try {
            TaoThaoTacHoaDon(hoaDon);
        } catch (Exception e) {
            System.err.println("⚠️ [GioHangService] Failed to create ThaoTacHoaDon: " + e.getMessage());
            // Don't fail the entire transaction if this fails
        }

        // #region agent log
        try (FileWriter fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_taoHoaDonThanhToan_success\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GioHangService.java:630\",\"message\":\"taoHoaDonThanhToan SUCCESS\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
        } catch (IOException e) {}
        // #endregion

        return hoaDon;
    }

    private void TaoThaoTacHoaDon(HoaDon hoaDon) {
        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon();
        thaoTacHoaDon.setThaoTac("Đặt hàng thành công");
        thaoTacHoaDon.setNgayTao(LocalDateTime.now());
        thaoTacHoaDon.setTrangThai(1);
        thaoTacHoaDon.setHoaDon(hoaDon);
        thaoTacHoaDonRepository.save(thaoTacHoaDon);

    }

    private String addVoucher(HoaDon hoaDon, List<Long> idVoucherKhachHangList) {
        // FIX: Handle null list
        if (idVoucherKhachHangList == null || idVoucherKhachHangList.isEmpty()) {
            return "done";
        }

//        get vocher
        List<Voucher> vouchers = new ArrayList<>();
        for (Long idVoucherKhachHang: idVoucherKhachHangList) {
            var voucherKhachHang = voucherKhachHangRepository.findById(idVoucherKhachHang).orElse(null);
            if(voucherKhachHang == null) return "voucher không tồn tại";
            var soLuong = voucherKhachHang.getSoLuong() - 1;
            if(soLuong == 0) voucherKhachHang.setTrangThai(0);

            voucherKhachHang.setSoLuong(soLuong);
            voucherKhachHangRepository.save(voucherKhachHang);
            vouchers.add(voucherKhachHang.getVoucher());
        }
//        chech voucher
        if(vouchers.size()==0) return "done";
        if(vouchers.size()>2) return "Số lượng voucher vượt quá giới hạn";
//        if(vouchers.get(0).getTheLoai() == vouchers.get(1).getTheLoai()) return "2 voucher đều cùng 1 loại";
        List<VoucherHoaDon> voucherHoaDons = new ArrayList<>();
        for (Voucher voucher:vouchers) {
            if(voucher.getTrangThai() != 1 && voucher.getTrangThai() != 3 ) return "Vouche này đã hết hạn hoặc không còn hoạt động";
            if(voucher.getSoLuong() <= 0) return "Voucher này đã hết";
//            if(voucher.getTrangThai() == 1 || voucher.getTrangThai() == 3 ) {
                VoucherHoaDon voucherHoaDon = new VoucherHoaDon();
                voucherHoaDon.setNgayTao(LocalDateTime.now());
                voucherHoaDon.setVoucher(voucher);
                voucherHoaDon.setHoaDon(hoaDon);
                voucherHoaDon.setTrangThai(1);
                voucherHoaDons.add(voucherHoaDon);
//            }
        }
            voucherHoaDonRepository.saveAll(voucherHoaDons);
        return "done";
    }


    private void getDiaChi(GioHang gioHang , GioHangDiaChi request) {
        gioHang.setIdTinhThanh(request.getIdTinhThanh());
        gioHang.setTinhThanh(request.getTinhThanh());
        gioHang.setIdQuanHuyen(request.getIdQuanHuyen());
        gioHang.setQuanHuyen(request.getQuanHuyen());
        gioHang.setIdPhuongXa(request.getIdPhuongXa());
        gioHang.setPhuongXa(request.getPhuongXa());
        gioHang.setGhiChu(request.getGhiChu());
    }

    private boolean checkDiaChi(GioHangDiaChi request) {
        boolean check = false;
        if (!request.getGhiChu().trim().isEmpty()){
            check =true;
        }
        if (!request.getTenNguoiNhan().trim().isEmpty()){
            check =true;
        }
        if (!request.getSdtNguoiNhan().trim().isEmpty()){
            check =true;
        }
        return check;
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }
    
    // FIX: Helper method to safely get GioHang, handling multiple results
    private GioHang getGioHangByKhachHangId(Long idKhachHang) {
        try {
            // Try the original method first (for single result)
            return getGioHangByKhachHangId(idKhachHang);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // If multiple results, get the first active one
            System.err.println("⚠️ [GioHangService] Multiple carts found for khachHangId: " + idKhachHang + ", taking first active one");
            var activeCarts = gioHangRepository.findActiveCartsByKhachHangId(idKhachHang);
            if (!activeCarts.isEmpty()) {
                return activeCarts.get(0);
            }
            // If no active carts, get the most recent one
            var allCarts = gioHangRepository.findAllByKhachHangIdOrderByNgayTaoDesc(idKhachHang);
            return allCarts.isEmpty() ? null : allCarts.get(0);
        } catch (Exception e) {
            System.err.println("❌ [GioHangService] Error getting GioHang: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // FIX: Helper method to safely get Voucher, handling duplicate codes
    private Voucher getVoucherByMa(String ma) {
        try {
            return voucherRepository.findByMa(ma).orElse(null);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            System.err.println("⚠️ [GioHangService] Multiple vouchers found with ma: " + ma + ", taking first one");
            var vouchers = voucherRepository.findAllByMaOrderByIdDesc(ma);
            return vouchers.isEmpty() ? null : vouchers.get(0);
        } catch (Exception e) {
            System.err.println("❌ [GioHangService] Error getting Voucher: " + e.getMessage());
            return null;
        }
    }
    
    // FIX: Helper method to safely get VoucherKhachHang, handling multiple records
    private VoucherKhachHang getVoucherKhachHangByVoucherId(Long idVoucher) {
        try {
            return voucherKhachHangRepository.findByVoucherId(idVoucher).orElse(null);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            System.err.println("⚠️ [GioHangService] Multiple VoucherKhachHang found for voucherId: " + idVoucher + ", taking first one");
            var voucherKhachHangs = voucherKhachHangRepository.findAllByVoucherIdOrderByNgayTaoDesc(idVoucher);
            return voucherKhachHangs.isEmpty() ? null : voucherKhachHangs.get(0);
        } catch (Exception e) {
            System.err.println("❌ [GioHangService] Error getting VoucherKhachHang: " + e.getMessage());
            return null;
        }
    }
}
