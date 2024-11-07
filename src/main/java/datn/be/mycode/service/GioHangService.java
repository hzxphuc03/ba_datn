package datn.be.mycode.service;

import datn.be.mycode.entity.GioHang;
import datn.be.mycode.entity.Voucher;
import datn.be.mycode.entity.VoucherGioHang;
import datn.be.mycode.entity.VoucherHoaDon;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.GioHang.GioHangDiaChi;
import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.request.GioHang.GioHangPhuongThucThanhToan;
import datn.be.mycode.request.GioHang.GioHangVoucher;
import datn.be.mycode.request.VoucherHoaDonRequest;
import datn.be.mycode.request.customRequest.BanHang.AddObject;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public ResponseEntity<?> thanhToan(){
        return null;
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
}
