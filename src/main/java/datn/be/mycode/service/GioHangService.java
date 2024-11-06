package datn.be.mycode.service;

import datn.be.mycode.entity.GioHang;
import datn.be.mycode.repository.GioHangChiTietRepository;
import datn.be.mycode.repository.GioHangRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.PhuongThucThanhToanRepository;
import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        gioHang.setNgayThanhToan(request.getNgayThanhToan());
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

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }
}
