package datn.be.mycode.RESTController;

import datn.be.mycode.request.HoaDonChiTietRequest;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.service.HoaDonChiTietService;
import datn.be.mycode.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/hoa_don_chi_tiet")
public class HoaDonChiTietCotroller {

    @Autowired
    private HoaDonChiTietService hoaDonChiTietService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "idHoaDon",required = false) Long keyWord,
            @RequestParam(name = "status",required = false) Integer status
    ){

        NormalTableRequest request = new NormalTableRequest(page,pageSize,keyWord+"",status);
        return ResponseEntity.ok(hoaDonChiTietService.getAllByIdHoaDon(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody HoaDonChiTietRequest request){
        System.out.println(request);
        return hoaDonChiTietService.add(request);
    }

//    , request.getStartDate(), request.getEndDate(), request.getIdKhachHang(), request.getIdNhanVien()
//, LocalDateTime startDate, LocalDateTime endDate, Long idKhachHang, Long idNhanVien


//    @PutMapping("update")
//    public ResponseEntity<?> update(@RequestBody HoaDonRequest request){
//        System.out.println(request);
//        return ResponseEntity.ok(hoaDonChiTietService.update(request));
//    }
//
//    @PutMapping("/update-trang-thai/{id}")
//    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
//                                             @RequestParam(name = "status",required = true) Integer status
//    ){
//
//        return ResponseEntity.ok(hoaDonChiTietService.udateTrangThai(id, status));
//    }
}
