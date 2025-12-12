package datn.be.mycode.RESTController;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.request.HoaDon.HoaDonByKhachHang;
import datn.be.mycode.request.HoaDon.HuyHoaDonRequest;
import datn.be.mycode.request.HoaDon.NextStep;
import datn.be.mycode.request.HoaDonRequest;

import datn.be.mycode.request.customRequest.BanHang.*;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.service.HoaDonService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/hoa_don")
public class HoaDonCotroller {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "startDate",required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate",required = false) LocalDateTime endDate,
            @RequestParam(name = "idKhachHang",required = false) Long idKhachHang,
            @RequestParam(name = "idNhanVien",required = false) Long idNhanVien,
            @RequestParam(name = "status",required = false) Integer status,
            HttpServletRequest httpRequest
    ){
        // #region agent log
        try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAll_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:30\",\"message\":\"getAll ENTRY\",\"data\":{\"page\":" + (page != null ? page : "null") + ",\"pageSize\":" + (pageSize != null ? pageSize : "null") + ",\"status\":" + (status != null ? status : "null") + ",\"requestURI\":\"" + httpRequest.getRequestURI() + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
        } catch (java.io.IOException e) {}
        // #endregion
        try {
            System.out.println("🔍 [HoaDonController] getAll called with params: page=" + page + ", idNhanVien=" + idNhanVien + ", status=" + status);
            
            TableHoaDonRequest request = new TableHoaDonRequest(page,pageSize,keyWord,startDate,endDate,idKhachHang,idNhanVien,status);
            var result = hoaDonService.getAllByTrangThai1(request);
            
            System.out.println("✅ [HoaDonController] getAll completed. Returning " + (result.getItem() != null ? result.getItem().size() : 0) + " items");
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAll_success\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:47\",\"message\":\"getAll SUCCESS\",\"data\":{\"itemCount\":" + (result.getItem() != null ? result.getItem().size() : 0) + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (java.io.IOException e) {}
            // #endregion
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAll_exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:49\",\"message\":\"getAll EXCEPTION\",\"data\":{\"exception\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + (e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "null") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (java.io.IOException e2) {}
            // #endregion
            System.err.println("❌ [HoaDonController] Error in getAll: " + e.getMessage());
            e.printStackTrace();
            
            // Return error response
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to get hoa don list: " + e.getMessage());
            error.put("data", new java.util.ArrayList<>());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllByTrangThai(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "startDate",required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate",required = false) LocalDateTime endDate,
            @RequestParam(name = "idKhachHang",required = false) Long idKhachHang,
            @RequestParam(name = "idNhanVien",required = false) Long idNhanVien,
            @RequestParam(name = "status",required = false) Integer status,
            HttpServletRequest httpRequest
    ){
        // #region agent log
        try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAllByTrangThai_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:63\",\"message\":\"getAllByTrangThai ENTRY\",\"data\":{\"page\":" + (page != null ? page : "null") + ",\"pageSize\":" + (pageSize != null ? pageSize : "null") + ",\"status\":" + (status != null ? status : "null") + ",\"requestURI\":\"" + httpRequest.getRequestURI() + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
        } catch (java.io.IOException e) {}
        // #endregion
        try {
            System.out.println("🔍 [HoaDonController] get-all called with params: page=" + page + ", status=" + status);
            
            TableHoaDonRequest request = new TableHoaDonRequest(page,pageSize,keyWord,startDate,endDate,idKhachHang,idNhanVien,status);
            var result = hoaDonService.getAll(request);
            
            System.out.println("✅ [HoaDonController] get-all completed. Returning " + (result.getItem() != null ? result.getItem().size() : 0) + " items");
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAllByTrangThai_success\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:80\",\"message\":\"getAllByTrangThai SUCCESS\",\"data\":{\"itemCount\":" + (result.getItem() != null ? result.getItem().size() : 0) + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (java.io.IOException e) {}
            // #endregion
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_getAllByTrangThai_exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:82\",\"message\":\"getAllByTrangThai EXCEPTION\",\"data\":{\"exception\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + (e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "null") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n");
            } catch (java.io.IOException e2) {}
            // #endregion
            System.err.println("❌ [HoaDonController] Error in get-all: " + e.getMessage());
            e.printStackTrace();
            
            // Return error response
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to get hoa don list: " + e.getMessage());
            error.put("data", new java.util.ArrayList<>());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/get-hoa-don-by-id-hoa-don/{idHoaDon}")
    public ResponseEntity<?> getByIdHoaDon(
            @PathVariable(name = "idHoaDon",required = false) Long idHoaDon
    ){
        return ResponseEntity.ok(hoaDonService.getById(idHoaDon));
    }

    @GetMapping("/get-hoa-don-by-khach-hang")
    public ResponseEntity<?> getByKhachHang(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "idKhachHang",required = false) Long idKhachHang
    ){
        HoaDonByKhachHang hoaDonByKhachHang = new HoaDonByKhachHang(page,pageSize,idKhachHang);
        return ResponseEntity.ok(hoaDonService.getByKhachHang(hoaDonByKhachHang));
    }

    @GetMapping("/get-hoa-don-hoan-thanh-by-khach-hang")
    public ResponseEntity<?> getHoaDonHoanThanhByKhachHang(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "idKhachHang",required = false) Long idKhachHang
    ){
        HoaDonByKhachHang hoaDonByKhachHang = new HoaDonByKhachHang(page,pageSize,idKhachHang);
        return ResponseEntity.ok(hoaDonService.getHoaDonHoanThanhByKhachHang(hoaDonByKhachHang));
    }

    @GetMapping("/get-hoa-don-chi-tiet-cho-view-khach-hang/{idHoaDon}")
    public ResponseEntity<?> getHoaDonChiTietChoViewKhachHang(@PathVariable Long idHoaDon) {

        return ResponseEntity.ok(hoaDonService.getHoaDonChiTietChoViewKhachHang(idHoaDon));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody HoaDonRequest request){

        return ResponseEntity.ok(hoaDonService.add(request));
    }

    @RequestMapping(value = "/add-nhanh", method = {RequestMethod.GET, RequestMethod.POST}) // FIX: Support both GET and POST
    public ResponseEntity<?> addFast(@RequestParam("idNhanVien") Long idNhanVien){
        // #region agent log
        try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
            fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_addFast_entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:167\",\"message\":\"addFast ENTRY\",\"data\":{\"idNhanVien\":" + idNhanVien + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
        } catch (java.io.IOException e) {}
        // #endregion
        
        try {
            HoaDon hoaDon = hoaDonService.add(idNhanVien);
            if (hoaDon == null) {
                // #region agent log
                try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                    fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_addFast_null\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:170\",\"message\":\"add returned null\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
                } catch (java.io.IOException e) {}
                // #endregion
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("success", false);
                        put("message", "Không thể tạo hóa đơn. Vui lòng kiểm tra thông tin nhân viên hoặc phương thức thanh toán.");
                        put("data", new java.util.ArrayList<>());
                    }});
            }
            
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_addFast_success\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:170\",\"message\":\"addFast SUCCESS\",\"data\":{\"hoaDonId\":" + hoaDon.getId() + "},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
            } catch (java.io.IOException e) {}
            // #endregion
            
            return ResponseEntity.ok(hoaDon);
        } catch (Exception e) {
            // #region agent log
            try (java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true)) {
                fw.write("{\"id\":\"log_" + System.currentTimeMillis() + "_hoaDon_addFast_exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"HoaDonCotroller.java:167\",\"message\":\"addFast EXCEPTION\",\"data\":{\"exception\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + (e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "null") + "\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1,H2\"}\n");
            } catch (java.io.IOException e2) {}
            // #endregion
            System.err.println("❌ [HoaDonController] Exception in addFast: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new java.util.HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "Lỗi hệ thống: " + e.getMessage());
                    put("data", new java.util.ArrayList<>());
                }});
        }
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody HoaDonRequest request){

        return ResponseEntity.ok(hoaDonService.update(request));
    }

    @PutMapping("/add-khach-hang")
    public ResponseEntity<?> addKhachHang(@RequestBody() AddObject request){

        return ResponseEntity.ok(hoaDonService.hoaDonAddKhachHang(request));
    }

    @PutMapping("/add-phuong-thuc-thanh-toan")
    public ResponseEntity<?> addPhuongThucThanhToan(@RequestBody() AddObject request){

        return ResponseEntity.ok(hoaDonService.hoaDonAddPhuongThucThanhToan(request));
    }

    @PutMapping("/add-dia-chi")
    public ResponseEntity<?> addDiaChi(@RequestBody() AddDiaChi request){

        return ResponseEntity.ok(hoaDonService.hoaDonAddDiaChi(request));
    }

    @PutMapping("/update-trang-thai-giao-hang")
    public ResponseEntity<?> isGiaoHangForHoaDon(@RequestBody() updateGiaoHang request){

        return ResponseEntity.ok(hoaDonService.isGiaoHangForHoaDon(request));
    }
    @PutMapping("/thanh-toan-tai-quay")
    public ResponseEntity<?> thanhToan(@RequestBody() ThanhToanRequest request){

        return ResponseEntity.ok(hoaDonService.thanhToan(request));
    }

    @PostMapping("/delete-voucher")
    public ResponseEntity<?> deleteVoucher(@RequestBody() DeleteVoucher request){

        return ResponseEntity.ok(hoaDonService.deleteVoucher(request));
    }

    @PutMapping("/add-voucher")
    public ResponseEntity<?> addVoucher(@RequestBody() AddVoucher request){

        return ResponseEntity.ok(hoaDonService.hoaDonAddVoucher(request));
    }

    @PutMapping("/next-step")
    public ResponseEntity<?> nextStep(@RequestBody() NextStep request){

        return ResponseEntity.ok(hoaDonService.nextSetp(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(hoaDonService.udateTrangThai(id, status));
    }

    @PostMapping("/huy-hoa-don")
    public ResponseEntity<?> huyHoaDon(@RequestBody HuyHoaDonRequest request){

        return ResponseEntity.ok(hoaDonService.huyHoaDon(request));
    }

    @PutMapping("/update-trang-thai-thanh_toan-online/{idHoaDon}")
    public ResponseEntity<?> updateTrangThaiThanhToan(@PathVariable() Long idHoaDon){

        return ResponseEntity.ok(hoaDonService.updateTrangThaiThanhToan(idHoaDon));
    }
}
