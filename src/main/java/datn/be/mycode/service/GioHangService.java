package datn.be.mycode.service;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.BanHangOnline.ThanhToanKhiNhanHang;
import datn.be.mycode.request.GioHang.GioHangDiaChi;
import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.request.GioHang.GioHangPhuongThucThanhToan;
import datn.be.mycode.request.GioHang.GioHangVoucher;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private VNPayService vnPayService;

    public ResponseEntity<?> getByKhachHang(Long idKhachHang) {
        var gioHang = gioHangRepository.findByKhachHangId(idKhachHang).orElse(null);
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
        if(khachHang == null) return getErro("Khách hàng này chưa tồn tại hoặc đã dừng hoạt động");
        var gioHang = gioHangRepository.findByKhachHangId(khachHang.getId()).orElse(null);
        if(khachHang == null) return getErro("Khách hàng này chưa tồn tại hoặc đã dừng hoạt động");

        var soLuong = gioHangChiTietRepository.getTongSoLuong(gioHang.getId());

        return ResponseEntity.ok(soLuong);
    }

    public ResponseEntity<?> addDiaChi(GioHangDiaChi request) {
        var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachHang()).orElse(null);
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
        var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachHang()).orElse(null);
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
        var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachHang()).orElse(null);
        var phuongThucThanhToan = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).orElse(null);

        if(gioHang == null) return getErro("Hóa đơn không tồn tại");
        if(phuongThucThanhToan == null) return getErro("Phương thức thanh toán không tồn tại");

        gioHang.setPhuongThucThanhToan(phuongThucThanhToan);

        return ResponseEntity.ok(gioHangRepository.save(gioHang));
    }

    public ResponseEntity<?> thanhToanOnline(ThanhToanKhiNhanHang request){
        var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(2)).orElse(null);
        var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachHang()).orElse(null);
        if(gioHang == null) return getErro("Khách hàng này đã dừng");

        HoaDon hoaDon = taoHoaDonThanhToan(request);
        hoaDon.setPhuongThucThanhToan(pttt);
        hoaDon.setTrangThai(0);
        hoaDon = hoaDonRepository.save(hoaDon);
//            toa hoa don chi tiet
        List<HoaDonChiTiet> hoaDonChiTietList = taoHoaDonChiTiet(hoaDon,request.getIdGioHangChiTietList());
        if(hoaDonChiTietList == null) return getErro("Chưa có sản phẩm trong giỏ hàng");
        setGiohang(gioHang);

        return ResponseEntity.ok(taoURLThanhToan(hoaDon.getTongTien().intValue(),hoaDon.getId()+""));

    }


    public ResponseEntity<?> thanhToanKhiGiaohang(ThanhToanKhiNhanHang request) {
        var pttt = phuongThucThanhToanRepository.findById(Long.valueOf(3)).orElse(null);
        var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachHang()).orElse(null);
        if(gioHang == null) return getErro("Khách hàng này đã dừng");

//        tao hoa don
        HoaDon hoaDon = taoHoaDonThanhToan(request);
        hoaDon.setPhuongThucThanhToan(pttt);
        hoaDon = hoaDonRepository.save(hoaDon);
//            toa hoa don chi tiet
        List<HoaDonChiTiet> hoaDonChiTietList = taoHoaDonChiTiet(hoaDon,request.getIdGioHangChiTietList());
        if(hoaDonChiTietList == null) return getErro("Chưa có sản phẩm trong giỏ hàng");
        setGiohang(gioHang);


        return ResponseEntity.ok(hoaDon);

    }

    public String taoURLThanhToan(int orderTotal,String maHoaDon) {
        return vnPayService.createOrder(orderTotal,maHoaDon);
    }


    public ResponseEntity<?> thanhToanThanhCong(Long idHoaDon, int trangThai) {
        var hoaDon = hoaDonRepository.findById(idHoaDon).orElse(null);
        if (hoaDon == null ) return getErro("Hóa đơn này không tồn tại");

        if(trangThai == 0){
            xoaHoaDon(hoaDon);

            return ResponseEntity.ok("Thanh toán thất bại");
        }

        hoaDon.setThanhToan(true);
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThai(2);
        hoaDonRepository.save(hoaDon);

        setSanPhamThanhToanOnline(hoaDon);

//        tra ve hoaDon va hoadonchitiet
        return ResponseEntity.ok("Thành Công");
    }

    private void xoaHoaDon(HoaDon hoaDon) {
        var hoaDonChiTiets = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        hoaDonChiTietRepository.deleteAll(hoaDonChiTiets);
        hoaDonRepository.delete(hoaDon);
    }

    private void setSanPhamThanhToanOnline(HoaDon hoaDon) {
        var hoaDonChiTiets = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        var gioHang = gioHangRepository.findByKhachHangId(hoaDon.getKhachHang().getId()).orElse(null);


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
        var gioHangChiTiets = new ArrayList<GioHangChiTiet>();
        for (Long idGioHangChiTiet:idGioHangChiTiets) {
            var gioHangChiTiet = gioHangChiTietRepository.findById(idGioHangChiTiet).get();
            gioHangChiTiets.add(gioHangChiTiet);
        }
        List<HoaDonChiTiet> hoaDonChiTiets = new ArrayList<>();
        for (GioHangChiTiet gioHangChiTiet :gioHangChiTiets) {

            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            hoaDonChiTiet.setGiaHienHanh(gioHangChiTiet.getGiaHienHanh());
            hoaDonChiTiet.setGiaDaGiam(gioHangChiTiet.getGiaDaGiam());
            hoaDonChiTiet.setTrangThai(1);
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(gioHangChiTiet.getSanPhamChiTiet());

            if (hoaDon.getPhuongThucThanhToan().getId() == 3) {
                setSanPham(gioHangChiTiet);
            }
            hoaDonChiTiets.add(hoaDonChiTiet);
        }

        return hoaDonChiTietRepository.saveAll(hoaDonChiTiets);
    }

    private void setSanPham(GioHangChiTiet gioHangChiTiet) {


            var sanPhamChiTiet = gioHangChiTiet.getSanPhamChiTiet();
            var sanPham = sanPhamChiTiet.getSanPham();
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - gioHangChiTiet.getSoLuong());
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

        return hoaDon;
    }

//    private HoaDon taoHoaDonThanhToanKhiNhanHang(GioHang gioHang){
//        HoaDon hoaDon = new HoaDon();
//
//        hoaDon.setNgayTao(LocalDateTime.now());
//        hoaDon.setGhiChu(gioHang.getGhiChu());
//        hoaDon.setTenNguoiNhan(gioHang.getTenNguoiNhan());
//        hoaDon.setSdtNguoiNhan(gioHang.getSdtNguoiNhan());
//        hoaDon.setIdTinhThanh(gioHang.getIdTinhThanh());
//        hoaDon.setTinhThanh(gioHang.getTinhThanh());
//        hoaDon.setIdQuanHuyen(gioHang.getIdQuanHuyen());
//        hoaDon.setQuanHuyen(gioHang.getQuanHuyen());
//        hoaDon.setIdPhuongXa(gioHang.getIdPhuongXa());
//        hoaDon.setPhuongXa(gioHang.getPhuongXa());
//        hoaDon.setPhiShip(gioHang.getPhiShip());
//        hoaDon.setTongTien(gioHang.getTongTien());
//        hoaDon.setTongTienSanPhamChuaGiam(gioHang.getTongTienSanPhamChuaGiam());
//        hoaDon.setTongTienGiam(gioHang.getTongTienGiam());
//        hoaDon.setThanhToan(false);
//        hoaDon.setGiaoHang(true);
//        hoaDon.setTrangThai(2);
//        hoaDon.setPhuongThucThanhToan(gioHang.getPhuongThucThanhToan());
//        hoaDon.setKhachHang(gioHang.getKhachHang());
//        hoaDon.setNhanVien(null);
//
//        hoaDon = hoaDonRepository.save(hoaDon);
//        hoaDon.setMa("HD"+hoaDon.getId());
//
//        return hoaDon;
//    }

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
}
