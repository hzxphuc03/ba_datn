package datn.be.mycode.RESTController;

import datn.be.mycode.request.ThongBao.ThongBaoGet;
import datn.be.mycode.service.ThongBaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/thong_bao")
public class ThongBaoController {

    @Autowired
    private ThongBaoService thongBaoService;

    @GetMapping("/get-thong-bao")
    public ResponseEntity<?> getThongBao(@RequestParam(name = "page",required = false) Integer page,
                                         @RequestParam(name = "pageSize",required = false) Integer pageSize,
                                         @RequestParam(name = "idKhachHang",required = false) Long idKhachHang){
        ThongBaoGet request = new ThongBaoGet(page, pageSize, idKhachHang);
        return ResponseEntity.ok(thongBaoService.getThongBao(request));
    }

    @GetMapping("/get-count-thong-bao/{idKhachHang}")
    public ResponseEntity<?> getCountThongBao(@PathVariable Long idKhachHang){
        return ResponseEntity.ok(thongBaoService.getCountThongBao(idKhachHang));
    }
}
