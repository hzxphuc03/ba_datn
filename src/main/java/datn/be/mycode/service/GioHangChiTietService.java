package datn.be.mycode.service;

import datn.be.mycode.entity.GioHangChiTiet;
import datn.be.mycode.repository.GioHangChiTietRepository;
import datn.be.mycode.repository.GioHangRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.SanPhamChiTietRepository;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.gioHangChiTIet.AddGioHangChiTietReq;
import datn.be.mycode.request.gioHangChiTIet.AddSoLuongReq;
import datn.be.mycode.request.gioHangChiTIet.GetAll;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.erroResponse;
import datn.be.mycode.response.gioHangChiTiet.AddGioHangChiTietRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GioHangChiTietService {

    @Autowired
    private GioHangChiTietRepository repo;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private GioHangService gioHangService; // Inject to use helper method
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<GioHangChiTiet> getAllByGioHang(Long request) {

        var gioHang = gioHangService.getGioHangByKhachHangId(request);
        if(gioHang == null) return null;

        var gioHangChiTiets = repo.findAllByGioHangId(gioHang.getId());

        return gioHangChiTiets;
    }

    public ResponseEntity<?> addGioHangChiTiet(AddGioHangChiTietReq request){
        try {
            // Tìm hoặc tạo gioHang nếu chưa có
            var gioHang = gioHangService.getGioHangByKhachHangId(request.getIdKhachhang());

            if (gioHang == null) {
                // Tạo mới gioHang cho khách hàng
                var khachHang = khachHangRepository.findById(request.getIdKhachhang()).orElse(null);
                if (khachHang == null) {
                    return getErro("Khách hàng này không tồn tại");
                }

                gioHang = new datn.be.mycode.entity.GioHang();
                gioHang.setKhachHang(khachHang);
                gioHang.setNgayTao(LocalDateTime.now());
                gioHang.setPhiShip(BigDecimal.ZERO);
                gioHang.setTongTien(BigDecimal.ZERO);
                gioHang.setTongTienGiam(BigDecimal.ZERO);
                gioHang.setTongTienSanPhamChuaGiam(BigDecimal.ZERO);
                gioHang.setTrangThai(1);
                gioHang = gioHangRepository.save(gioHang);
                gioHang.setMa("GH" + gioHang.getId());
                gioHang = gioHangRepository.save(gioHang);
            }

            // Kiểm tra trangThai sau khi đã có gioHang
            if (gioHang.getTrangThai() == 0) {
                return getErro("Giỏ hàng này đã bị khóa");
            }

            var gioHangChiTiet = repo.findByGioHangIdAndAndSanPhamChiTietId(gioHang.getId(), request.getIdSanPhamChiTiet()).orElse(null);
            var sanPhamChiTiet = sanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null);
            if (sanPhamChiTiet == null || sanPhamChiTiet.getTrangThai() == 0) {
                return getErro("Sản phẩm này hiện đang hết hàng hoặc không có");
            }
            var giamGia = sanPhamChiTiet.getSanPham().getGiamGia();
            var gia = sanPhamChiTiet.getGiaBan();
            var giaDaGiam = new BigDecimal(gia.toString());

            if(request.getSoLuong() <= 0) return getErro("Số lượng phải lớn hơn 0");
            if(request.getSoLuong()>sanPhamChiTiet.getSoLuong()) return getErro("Số lượng sản phẩm không vượt quá số lượng đang có");

            if(giamGia != null && giamGia.getTrangThai() == 1){
                var mucGiam = BigDecimal.valueOf(giamGia.getMucGiam()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                giaDaGiam = gia.subtract(gia.multiply(mucGiam));
            }

            if(gioHangChiTiet == null) {
                gioHangChiTiet = new GioHangChiTiet();
                gioHangChiTiet.setSoLuong(request.getSoLuong());
                gioHangChiTiet.setGiaHienHanh(gia);
                gioHangChiTiet.setGiaDaGiam(giaDaGiam);
                gioHangChiTiet.setTrangThai(1);
                gioHangChiTiet.setGioHang(gioHang);
                gioHangChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            } else {
                gioHangChiTiet.addSoLuong(request.getSoLuong());
                if(gioHangChiTiet.getSoLuong() > sanPhamChiTiet.getSoLuong()) return getErro("Số lượng sản phẩm không vượt quá số lượng đang có");
            }

            gioHangChiTiet= repo.save(gioHangChiTiet);
            AddGioHangChiTietRes GioHangChiTietRes = new AddGioHangChiTietRes(gioHangChiTiet);

            return ResponseEntity.ok(GioHangChiTietRes);
        } catch (Exception e) {
            System.err.println("❌ [GioHangChiTietService] Error in addGioHangChiTiet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new erroResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> addSoLuong(AddSoLuongReq request) {
        var gioHangChiTiet = repo.findById(request.getIdGioHangChiTiet()).orElse(null);
        if(gioHangChiTiet == null) return getErro("Gio hang này không tồn tại");
        var spct = gioHangChiTiet.getSanPhamChiTiet();
        var soLuong = request.getSoLuong();

        if(soLuong == 0)return getErro("Số lượng phải lớn hơn 0");
        if(soLuong > spct.getSoLuong()) return getErro("số lượng vượt quá số lượng sản phẩm có");

        gioHangChiTiet.setSoLuong(soLuong);
        return ResponseEntity.ok(repo.save(gioHangChiTiet));
    }

    public ResponseEntity<?> delete(Long id){
        var gioHangChiTiet = repo.findById(id).orElse(null);
        if (gioHangChiTiet == null )return getErro("Giỏ hàng này không tồn tại");
        repo.delete(gioHangChiTiet);
        return ResponseEntity.ok("Xóa thành công");
    }

    public ResponseEntity<?> deleteAllByGioHang(Long idKhachHang) {
        var gioHang = gioHangService.getGioHangByKhachHangId(idKhachHang);
        var gioHangChiTiets = repo.findAllByGioHangId(gioHang.getId());
        if (gioHangChiTiets == null )return getErro("Giỏ hàng này không tồn tại");
        repo.deleteAll(gioHangChiTiets);
        return ResponseEntity.ok("Xóa thành công");
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
