package datn.be.mycode.RESTController;

import datn.be.mycode.request.TableVoucherRequest;
import datn.be.mycode.request.VoucherRequest;
import datn.be.mycode.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/voucher")
public class VoucherController {
    @Autowired
    private VoucherService service;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status,
            @RequestParam(name = "startDate",required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate",required = false) LocalDateTime endDate,
            @RequestParam(name = "phanTramGiam",required = false) Integer phanTramGiam
    ){

        TableVoucherRequest request = new TableVoucherRequest(page,pageSize,keyWord,status,startDate,endDate,phanTramGiam);
        return ResponseEntity.ok(service.getAll(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody VoucherRequest tenVoucherRequest){
        System.out.println(tenVoucherRequest);
        return ResponseEntity.ok(service.add(tenVoucherRequest));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody VoucherRequest request){

        return ResponseEntity.ok(service.update(request));
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(service.udateTrangThai(id, status));
    }


}
