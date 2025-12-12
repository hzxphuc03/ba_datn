package datn.be.mycode.RESTController;

import datn.be.mycode.request.BanHangOnline.ThanhToanKhiNhanHang;
import datn.be.mycode.request.GioHang.GioHangDiaChi;
import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.request.GioHang.GioHangPhuongThucThanhToan;
import datn.be.mycode.request.GioHang.GioHangVoucher;
import datn.be.mycode.request.customRequest.BanHang.AddObject;
import datn.be.mycode.request.customRequest.BanHang.AddVoucher;
import datn.be.mycode.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/gio_hang")
public class GiohangCotroller {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping("/{idKhachHang}")
    public ResponseEntity<?> getAll(@PathVariable("idKhachHang") Long idKhachHang){
        return ResponseEntity.ok(gioHangService.getByKhachHang(idKhachHang).getBody());
    }

    @GetMapping("/get_so_luong/{idKhachHang}")
    public ResponseEntity<?> getSoLuong(@PathVariable("idKhachHang") Long idKhachHang){
        return ResponseEntity.ok(gioHangService.getSoLuong(idKhachHang).getBody());
    }

    @PutMapping("/add-dia-chi")
    public ResponseEntity<?> addDiaChi(@RequestBody() GioHangDiaChi request){

        return ResponseEntity.ok(gioHangService.addDiaChi(request));
    }

    @PutMapping("/add-voucher")
    public ResponseEntity<?> addVoucher(@RequestBody() GioHangVoucher request){

        return ResponseEntity.ok(gioHangService.addVoucher(request));
    }

    @PutMapping("/add-phuong-thuc-thanh-toan")
    public ResponseEntity<?> addPhuongThucThanhToan(@RequestBody() GioHangPhuongThucThanhToan request){

        return ResponseEntity.ok(gioHangService.addPhuongThucThanhToan(request));
    }

    @PutMapping("/thanh-toan-khi-giao-hang")
    public ResponseEntity<?> thanhToanKhiGiaohang(@RequestBody() ThanhToanKhiNhanHang request){
        // #region agent log
        try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_controller_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GiohangCotroller.java:53\",\"message\":\"Controller ENTRY\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H3\"}\n");
        } catch (java.io.IOException e) {}
        // #endregion
        ResponseEntity<?> response = gioHangService.thanhToanKhiGiaohang(request);
        // #region agent log
        try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_controller_exit\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"GiohangCotroller.java:55\",\"message\":\"Controller EXIT\",\"data\":{\"statusCode\":" + response.getStatusCode().value() + ",\"hasBody\":" + (response.getBody() != null) + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H3\"}\n");
        } catch (java.io.IOException e) {}
        // #endregion
        // FIX: Return service response directly instead of wrapping in 200 OK
        // This ensures error status codes (400, 500) are properly returned to frontend
        return response;
    }

    @PutMapping("/thanh-toan-online")
    public ResponseEntity<?> thanhToanOnline(@RequestBody() ThanhToanKhiNhanHang request){

        return ResponseEntity.ok(gioHangService.thanhToanOnline(request));
    }

    @PutMapping("/thanh-toan-thanh-cong")
    public ResponseEntity<?> thanhToanThanhCong(@RequestBody() ThanhToanKhiNhanHang request){

        return ResponseEntity.ok(gioHangService.thanhToanThanhCong(request));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateGioHang(@RequestBody() GioHangAdd request){
        ResponseEntity<?> responseEntity = gioHangService.updateGioHang(request);
        return responseEntity;
    }

}
