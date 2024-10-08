package datn.be.mycode.RESTController;

import datn.be.mycode.repository.SanPhamRepository;
import datn.be.mycode.request.ChucVuRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.SanPhamChiTietRequest;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.service.ChucVuService;
import datn.be.mycode.service.SanPhamChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/san_pham_chi_tiet")
public class SanPhamChiTietController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status,
            @RequestParam(name = "startDate",required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate",required = false) LocalDateTime endDate,
            @RequestParam(name = "sanPham",required = false) Long idSanPham,
            @RequestParam(name = "mauSac",required = false) Long idMauSac,
            @RequestParam(name = "kichCo",required = false) Long idKichCo,
            @RequestParam(name = "sort",required = false) Integer sort
    ){

        TableSanPhamChiTietRequest request = new TableSanPhamChiTietRequest(page, pageSize, keyWord, status, startDate, endDate, idSanPham, idMauSac, idKichCo,  sort);
        return ResponseEntity.ok(sanPhamChiTietService.getAll(request));
    }

    @GetMapping("/get-by-idSanPham/{idSanPham}")
    public ResponseEntity<?> getByIdSanPham(
            @PathVariable(name = "idSanPham",required = false) Long idSanPham
    ){
        return ResponseEntity.ok(sanPhamChiTietService.getByIdSanPham(idSanPham));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody SanPhamChiTietRequest request){

        return ResponseEntity.ok(sanPhamChiTietService.addSPCT(request));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody SanPhamChiTietRequest request){

        return ResponseEntity.ok(sanPhamChiTietService.update(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(sanPhamChiTietService.udateTrangThai(id, status));
    }

}
