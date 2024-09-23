package datn.be.mycode.RESTController;

import datn.be.mycode.request.SanPhamRequest;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.service.ChucVuService;
import datn.be.mycode.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/san_pham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status,
            @RequestParam(name = "startDate",required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate",required = false) LocalDateTime endDate,
            @RequestParam(name = "sort",required = false) Integer sort
    ){

        TableSanPhamRequest request = new TableSanPhamRequest(page,pageSize,keyWord,status, startDate, endDate, sort);
        return ResponseEntity.ok(sanPhamService.getAll(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody SanPhamRequest sanPhamRequest){

        return ResponseEntity.ok(sanPhamService.add(sanPhamRequest));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody SanPhamRequest request){

        return ResponseEntity.ok(sanPhamService.update(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){
        return ResponseEntity.ok(sanPhamService.udateTrangThai(id, status));
    }

}
