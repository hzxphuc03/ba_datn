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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GioHangChiTietService {

    @Autowired
    private GioHangChiTietRepository repo;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<GioHangChiTiet> getAllByGioHang(Long request) {

        var gioHang = gioHangRepository.findByKhachHangId(request).orElse(null);
        if(gioHang == null) return null;

        var gioHangChiTiets = repo.findAllByGioHangId(gioHang.getId());

        return gioHangChiTiets;
    }

    public ResponseEntity<?> addGioHangChiTiet(AddGioHangChiTietReq request){
        // #region agent log
        try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:49", "message", "Method entry", "data", java.util.Map.of("soLuong", request.getSoLuong(), "idKhachhang", request.getIdKhachhang(), "idSanPhamChiTiet", request.getIdSanPhamChiTiet()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "A");
            try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
        } catch (Exception e) {}
        // #endregion
        try {
            // Kiểm tra số lượng đầu tiên
            if(request.getSoLuong() <= 0) return getErro("Số lượng phải lớn hơn 0");
            
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:54", "message", "Before findByKhachHangId", "data", java.util.Map.of("idKhachhang", request.getIdKhachhang()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "B");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            var gioHang = gioHangRepository.findByKhachHangId(request.getIdKhachhang()).orElse(null);
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:55", "message", "After findByKhachHangId", "data", java.util.Map.of("gioHang", gioHang != null ? java.util.Map.of("id", gioHang.getId(), "trangThai", gioHang.getTrangThai()) : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "B");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            if(gioHang == null || gioHang.getTrangThai() == 0) return getErro("Khách hàng này không tồn tại hoặc đã nghỉ");
            
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:56", "message", "Before findByGioHangIdAndAndSanPhamChiTietId", "data", java.util.Map.of("gioHangId", gioHang.getId(), "sanPhamChiTietId", request.getIdSanPhamChiTiet()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "A");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            var gioHangChiTiet = repo.findByGioHangIdAndAndSanPhamChiTietId(gioHang.getId(),request.getIdSanPhamChiTiet()).orElse(null);
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:57", "message", "After findByGioHangIdAndAndSanPhamChiTietId", "data", java.util.Map.of("gioHangChiTiet", gioHangChiTiet != null ? "exists" : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "A");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:57b", "message", "Before findById sanPhamChiTiet", "data", java.util.Map.of("idSanPhamChiTiet", request.getIdSanPhamChiTiet()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "C");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            var sanPhamChiTiet = sanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null);
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:58", "message", "After findById sanPhamChiTiet", "data", java.util.Map.of("sanPhamChiTiet", sanPhamChiTiet != null ? java.util.Map.of("id", sanPhamChiTiet.getId(), "trangThai", sanPhamChiTiet.getTrangThai(), "soLuong", sanPhamChiTiet.getSoLuong()) : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "C");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            if (sanPhamChiTiet == null || sanPhamChiTiet.getTrangThai() == 0) return getErro("Sản phẩm này hiện đang hết hàng hoặc không có");
            
            // Kiểm tra null cho sanPham
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:61", "message", "Before accessing sanPham", "data", java.util.Map.of("sanPhamChiTietId", sanPhamChiTiet.getId()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "C");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            if(sanPhamChiTiet.getSanPham() == null) {
                return getErro("Sản phẩm chi tiết không có thông tin sản phẩm");
            }
            
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:65", "message", "Before accessing giamGia", "data", java.util.Map.of("sanPhamId", sanPhamChiTiet.getSanPham().getId()), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "C");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            var giamGia = sanPhamChiTiet.getSanPham().getGiamGia();
            var gia = sanPhamChiTiet.getGiaBan();
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:66", "message", "After getting giamGia and gia", "data", java.util.Map.of("giamGia", giamGia != null ? "exists" : "null", "gia", gia != null ? gia.toString() : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "C");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            
            // Kiểm tra null cho giaBan
            if(gia == null) {
                return getErro("Sản phẩm này chưa có giá bán");
            }
            
            var giaDaGiam = new BigDecimal(gia.toString());

            if(request.getSoLuong()>sanPhamChiTiet.getSoLuong()) return getErro("Số lượng sản phẩm không vượt quá số lượng đang có");

            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:77", "message", "Before giamGia calculation", "data", java.util.Map.of("giamGia", giamGia != null ? java.util.Map.of("trangThai", giamGia.getTrangThai(), "mucGiam", giamGia.getMucGiam()) : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "D");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            if(giamGia != null && giamGia.getTrangThai() == 1){
                var mucGiam = BigDecimal.valueOf(giamGia.getMucGiam()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                giaDaGiam = gia.subtract(gia.multiply(mucGiam));
            }

            if(gioHangChiTiet == null) {
                gioHangChiTiet = new GioHangChiTiet();
                gioHangChiTiet.setId(null); // Đảm bảo id là null để Hibernate không insert
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

            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:95", "message", "Before repo.save", "data", java.util.Map.of("gioHangChiTietId", gioHangChiTiet.getId(), "soLuong", gioHangChiTiet.getSoLuong(), "giaHienHanh", gioHangChiTiet.getGiaHienHanh() != null ? gioHangChiTiet.getGiaHienHanh().toString() : "null"), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "E");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            gioHangChiTiet= repo.save(gioHangChiTiet);
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:96", "message", "After repo.save", "data", java.util.Map.of("gioHangChiTietId", gioHangChiTiet.getId(), "success", true), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "E");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception e) {}
            } catch (Exception e) {}
            // #endregion
            AddGioHangChiTietRes GioHangChiTietRes = new AddGioHangChiTietRes(gioHangChiTiet);

            return ResponseEntity.ok(GioHangChiTietRes);
        } catch (Exception e) {
            // #region agent log
            try {
                var logData = java.util.Map.of("location", "GioHangChiTietService.addGioHangChiTiet:catch", "message", "Exception caught", "data", java.util.Map.of("exceptionType", e.getClass().getName(), "exceptionMessage", e.getMessage(), "stackTrace", java.util.Arrays.stream(e.getStackTrace()).limit(5).map(StackTraceElement::toString).collect(java.util.stream.Collectors.toList())), "timestamp", System.currentTimeMillis(), "sessionId", "debug-session", "runId", "run1", "hypothesisId", "ALL");
                try (var fw = new FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) { fw.write(new ObjectMapper().writeValueAsString(logData) + "\n"); } catch (Exception ex) {}
            } catch (Exception ex) {}
            // #endregion
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new erroResponse("Lỗi hệ thống: " + e.getMessage()));
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
        var gioHang = gioHangRepository.findByKhachHangId(idKhachHang).orElse(null);
        var gioHangChiTiets = repo.findAllByGioHangId(gioHang.getId());
        if (gioHangChiTiets == null )return getErro("Giỏ hàng này không tồn tại");
        repo.deleteAll(gioHangChiTiets);
        return ResponseEntity.ok("Xóa thành công");
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
