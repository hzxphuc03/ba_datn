package datn.be.mycode.RESTController;

import datn.be.mycode.request.HoaDonRequest;

import datn.be.mycode.request.customRequest.BanHang.*;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
            @RequestParam(name = "status",required = false) Integer status
    ){

        TableHoaDonRequest request = new TableHoaDonRequest(page,pageSize,keyWord,startDate,endDate,idKhachHang,idNhanVien,status);
        return ResponseEntity.ok(hoaDonService.getAllByTrangThai1(request));
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
            @RequestParam(name = "status",required = false) Integer status
    ){

        TableHoaDonRequest request = new TableHoaDonRequest(page,pageSize,keyWord,startDate,endDate,idKhachHang,idNhanVien,status);
        return ResponseEntity.ok(hoaDonService.getAll(request));
    }

    @GetMapping("/get-hoa-don-by-id-hoa-don/{idHoaDon}")
    public ResponseEntity<?> getByIdHoaDon(
            @PathVariable(name = "idHoaDon",required = false) Long idHoaDon
    ){
        return ResponseEntity.ok(hoaDonService.getById(idHoaDon));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody HoaDonRequest request){

        return ResponseEntity.ok(hoaDonService.add(request));
    }

    @PostMapping("/add-nhanh")
    public ResponseEntity<?> addFast(@RequestParam("idNhanVien") Long idNhanVien){

        return ResponseEntity.ok(hoaDonService.add(idNhanVien));
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

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(hoaDonService.udateTrangThai(id, status));
    }

    @PutMapping("/update-trang-thai-thanh_toan-online/{idHoaDon}")
    public ResponseEntity<?> updateTrangThaiThanhToan(@PathVariable() Long idHoaDon){

        return ResponseEntity.ok(hoaDonService.updateTrangThaiThanhToan(idHoaDon));
    }
}
