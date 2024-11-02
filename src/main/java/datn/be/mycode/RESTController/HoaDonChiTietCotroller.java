package datn.be.mycode.RESTController;

import datn.be.mycode.request.HoaDonChiTietRequest;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.customRequest.BanHang.AddSoLuong;
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
        return hoaDonChiTietService.add(request);
    }

    @PostMapping("/add-so-luong")
    public ResponseEntity<?> addSoLuong(@RequestBody AddSoLuong request){
        return hoaDonChiTietService.addSoLuong(request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        return hoaDonChiTietService.delete(id);
    }
}
