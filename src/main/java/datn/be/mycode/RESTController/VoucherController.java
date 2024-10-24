package datn.be.mycode.RESTController;


import datn.be.mycode.request.VoucherRequest;
import datn.be.mycode.request.customRequest.TableVoucherRequest;
import datn.be.mycode.request.customRequest.TableVoucherStatusInRequest;
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

    @GetMapping("/get-by-id/{idVoucher}")
    public ResponseEntity<?> getById(
            @PathVariable(name = "idVoucher",required = false) Long idVoucher
    ){
        return ResponseEntity.ok(service.getVoucherById(idVoucher));
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

    @PutMapping("/update-trangthai-auto")
    public ResponseEntity<?> capNhatTrangThaiVoucher() {
        // Gọi phương thức service để thực thi logic cập nhật trạng thái
        service.capNhatTrangThaiVoucher();

        // Trả về phản hồi thành công
        return ResponseEntity.ok("Cập nhật trạng thái voucher tự động thành công.");
    }

    @GetMapping("trong-su-kien")
    public ResponseEntity<?> trongSuKien(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "status",required = false) Integer status
    ){

        TableVoucherStatusInRequest request = new TableVoucherStatusInRequest(page,pageSize,status);
        return ResponseEntity.ok(service.trongSuKien(request));
    }

}
