package datn.be.mycode.RESTController;

import datn.be.mycode.request.DoiTra.PhieuDoiTra.AddPhieuDoiTraReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.GetPhieuDoiTraReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.KiemTraHangReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.XacNhanReq;
import datn.be.mycode.service.PhieuDoiTraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/doi-tra")
public class DoiTraController {

    @Autowired
    private PhieuDoiTraService phieuDoiTraService;

    @GetMapping("/get-phieu-doi-tra-admin")
    public ResponseEntity<?> getPhieuDoiTra(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "maPhieuGiamGia",required = false) String maPhieuGiamGia,
            @RequestParam(name = "status",required = false) Integer status
    ){
        GetPhieuDoiTraReq getPhieuDoiTraReq = new GetPhieuDoiTraReq(page, pageSize, keyWord, maPhieuGiamGia, status);
        return ResponseEntity.ok(phieuDoiTraService.getPhieuDoiTra(getPhieuDoiTraReq));
    }

    @PostMapping("/add-pheu-doi-tra")
    public ResponseEntity<?> addPhieuDoiTra(@RequestBody AddPhieuDoiTraReq request){
        return ResponseEntity.ok(phieuDoiTraService.addPhieuDoiTra(request));
    }

    @PutMapping("/nhan-vien-xac-nhan-phieu-doi-tra")
    public ResponseEntity<?> xacNhanPhieuDoiTra(@RequestBody XacNhanReq request){
        return ResponseEntity.ok(phieuDoiTraService.xacNhanPhieuDoiTra(request, 2));
    }

    @PutMapping("/nhan-vien-huy-phieu-doi-tra")
    public ResponseEntity<?> huyPhieuDoiTra(@RequestBody XacNhanReq request){
        return ResponseEntity.ok(phieuDoiTraService.xacNhanPhieuDoiTra(request, 0));
    }

//    @PostMapping("/nhan-vien-kiem-tra-hang")
//    public ResponseEntity<?> kiemTraHang(@RequestBody KiemTraHangReq request){
//        return ResponseEntity.ok(phieuDoiTraService.xacNhanPhieuDoiTra(request, 0));
//    }
}
