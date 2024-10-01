package datn.be.mycode.RESTController;

import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.NhanVienRequest;

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
        return ResponseEntity.ok(hoaDonService.getAll(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody HoaDonRequest request){
        System.out.println(request);
        return ResponseEntity.ok(hoaDonService.add(request));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody HoaDonRequest request){
        System.out.println(request);
        return ResponseEntity.ok(hoaDonService.update(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(hoaDonService.udateTrangThai(id, status));
    }
}
