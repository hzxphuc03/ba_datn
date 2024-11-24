package datn.be.mycode.RESTController;

import datn.be.mycode.request.NhanTin.NhanVien.HopThoaiNhanVien.AddHopThoai;
import datn.be.mycode.service.HopThoaiNhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hop-thoai")
public class HopThoaiController {

    @Autowired
    private HopThoaiNhanVienService hopThoaiNhanVienService;

    @PostMapping("/add-hop-thoai-nhan-vien")
    public ResponseEntity<?> addHopThoaiNhanVien(@RequestBody AddHopThoai request){
        return ResponseEntity.ok(hopThoaiNhanVienService.addHopThoaiNhanVien(request));
    }

//    @PutMapping("/update-hop-thoai-nhan-vien")
//    public ResponseEntity<?> updateHopThoaiNhanVien(@RequestBody AddHopThoai request){
//        return ResponseEntity.ok(hopThoaiNhanVienService.updateHopThoaiNhanVien(request));
//    }
}
