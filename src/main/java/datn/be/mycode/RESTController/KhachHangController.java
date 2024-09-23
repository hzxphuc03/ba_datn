package datn.be.mycode.RESTController;

import datn.be.mycode.request.KhachHangRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.NormalTableRequestKhachHang;
import datn.be.mycode.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/khach_hang")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status,
            @RequestParam(name = "soDienThoai",required = false) String soDienThoai,
            @RequestParam(name = "taiKhoan",required = false) String taiKhoan,
            @RequestParam(name = "email",required = false) String email

    ){

        NormalTableRequestKhachHang request = new NormalTableRequestKhachHang(page,pageSize,keyWord,status,soDienThoai,taiKhoan,email);
        return ResponseEntity.ok(khachHangService.getAll(request));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody KhachHangRequest tenKhachHangRequest){
        System.out.println(tenKhachHangRequest);
        return ResponseEntity.ok(khachHangService.add(tenKhachHangRequest));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody KhachHangRequest request){

        return ResponseEntity.ok(khachHangService.update(request));
    }



    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(khachHangService.udateTrangThai(id, status));
    }
}
