package datn.be.mycode.RESTController;

import datn.be.mycode.request.SuKienRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.service.SuKienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/su_kien")
public class SuKienController {
    @Autowired
    SuKienService service;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) Long keyWord,
            @RequestParam(name = "status",required = false) Integer status
    ){

        TableLongRequest request = new TableLongRequest(page,pageSize,keyWord,status);
        return ResponseEntity.ok(service.getAll(request));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody SuKienRequest suKienRequest){
        System.out.println(suKienRequest);
        return ResponseEntity.ok(service.add(suKienRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody SuKienRequest request){

        return ResponseEntity.ok(service.update(request));
    }

    @PutMapping("/update-trangthai-auto")
    public ResponseEntity<?> capNhatTrangThaiSuKien() {
        service.capNhatTrangThaiSuKien();
        return ResponseEntity.ok("Cập nhật trạng thái sự kiện tự động thành công.");
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer trangThai){
        return ResponseEntity.ok(service.udateTrangThai(id, trangThai));
    }
}
