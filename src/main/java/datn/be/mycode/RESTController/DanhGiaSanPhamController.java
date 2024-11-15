package datn.be.mycode.RESTController;

import datn.be.mycode.entity.DanhGiaSanPham;
import datn.be.mycode.request.DanhGiaSanPhamRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.service.DanhGiaSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody DanhGiaSanPhamRequest danhGiaSanPhamRequest){
        System.out.println(danhGiaSanPhamRequest);
        return ResponseEntity.ok(danhGiaSanPhamService.add(danhGiaSanPhamRequest));
    }

    @PostMapping("/danhgia-tatca")
    public ResponseEntity<?> danhGiaTatCaSanPham(
            @RequestParam Long idKhachHang,
            @RequestParam int sao,
            @RequestParam String cmt,
            @RequestParam String listHinhAnh) {
            List<DanhGiaSanPham> danhGiaList = danhGiaSanPhamService.addAllDanhGia(idKhachHang, sao, cmt, listHinhAnh);
            return ResponseEntity.ok(danhGiaList);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody DanhGiaSanPhamRequest request){

        return ResponseEntity.ok(danhGiaSanPhamService.update(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer trangThai){
        return ResponseEntity.ok(danhGiaSanPhamService.udateTrangThai(id, trangThai));
    }
}
