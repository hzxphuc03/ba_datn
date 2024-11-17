package datn.be.mycode.RESTController;

import datn.be.mycode.entity.DanhGiaSanPham;
import datn.be.mycode.request.DanhGiaSanPhamRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.request.khachHang.DanhGiaSanPhamChungRequest;
import datn.be.mycode.service.DanhGiaSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danh_gia_san_pham")
public class DanhGiaSanPhamController {
    @Autowired
    DanhGiaSanPhamService danhGiaSanPhamService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) Long keyWord,
            @RequestParam(name = "status",required = false) Integer status
    ){

        TableLongRequest request = new TableLongRequest(page,pageSize,keyWord,status);
        return ResponseEntity.ok(danhGiaSanPhamService.getAll(request));
    }


    @GetMapping("/get-by-khach-hang-id")
    public ResponseEntity<?> getDanhGiaByKhachHangId(
            @RequestParam Long idKhachHang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DanhGiaSanPham> danhGiaPage = danhGiaSanPhamService.getDanhGiaByKhachHangId(idKhachHang, page, size);
        return ResponseEntity.ok(danhGiaPage);
    }

//    @PutMapping("/update")
//    public ResponseEntity<?> update(@RequestBody DanhGiaSanPhamRequest request){
//
//        return ResponseEntity.ok(danhGiaSanPhamService.update(request));
//    } hop/15/11/2024

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer trangThai){
        return ResponseEntity.ok(danhGiaSanPhamService.udateTrangThai(id, trangThai));
    }

    @PostMapping("/add-danh-gia-theo-id-chi-tiet-hoa-don")
    public ResponseEntity<?> addDanhGiaTheoIDChiTietHoaDon(@RequestParam Long idChiTietHoaDon,
                                                        @RequestBody DanhGiaSanPhamChungRequest request) {

        DanhGiaSanPham danhGiaSanPham = danhGiaSanPhamService.danhGiaTheoIDChiTietHoaDon(idChiTietHoaDon, request);

        return ResponseEntity.ok(danhGiaSanPham);
    }

    @PostMapping("/add-danh-gia-chung")
    public ResponseEntity<?> addDanhGiaChungByIDKH(@RequestParam Long idKhachHang,
                                                   @RequestBody DanhGiaSanPhamChungRequest request) {

        List<DanhGiaSanPham> danhGiaList = danhGiaSanPhamService.danhGiaChungByIDKH(idKhachHang, request);

        return ResponseEntity.ok(danhGiaList);
    }

}
