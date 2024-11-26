package datn.be.mycode.RESTController;

import datn.be.mycode.request.NhanTin.KhachHang.HopThoai.AddHopThoaiKhachHang;
import datn.be.mycode.request.NhanTin.NhanVien.HopThoaiNhanVien.AddHopThoaiNhanVien;
import datn.be.mycode.service.HopThoaiKhachHangService;
import datn.be.mycode.service.HopThoaiNhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hop-thoai")
public class HopThoaiController {

    @Autowired
    private HopThoaiNhanVienService hopThoaiNhanVienService;
    @Autowired
    private HopThoaiKhachHangService hopThoaiKhachHangService;

    @GetMapping("/check-hop-thoai-nhan-vien")
    public ResponseEntity<?> checkHopThoaiNhanVien(
            @RequestParam(value = "idNhanVien1", required = false) Long idNhanVien1,
            @RequestParam(value = "idNhanVien2",required = false) Long idNhanVien2
    ){
        AddHopThoaiNhanVien request = new AddHopThoaiNhanVien(idNhanVien1, idNhanVien2);
        return ResponseEntity.ok(hopThoaiNhanVienService.checkHopThoaiNhanVien(request));
    }

    @PostMapping("/add-hop-thoai-nhan-vien")
    public ResponseEntity<?> addHopThoaiNhanVien(@RequestBody AddHopThoaiNhanVien request){
        return ResponseEntity.ok(hopThoaiNhanVienService.addHopThoaiNhanVien(request));
    }

//
//    @PostMapping("/add-hop-thoai-khach-hang")
//    public ResponseEntity<?> addHopThoaiKhachHang(@RequestBody AddHopThoaiKhachHang request) {
//        return ResponseEntity.ok(hopThoaiKhachHangService.addHopThoaiKhachHang(request));
//    }
}
