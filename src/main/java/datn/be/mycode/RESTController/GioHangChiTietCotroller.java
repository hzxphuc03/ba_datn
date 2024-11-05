package datn.be.mycode.RESTController;

import datn.be.mycode.repository.GioHangChiTietRepository;
import datn.be.mycode.request.HoaDonChiTietRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.customRequest.BanHang.AddSoLuong;
import datn.be.mycode.request.gioHangChiTIet.AddGioHangChiTietReq;
import datn.be.mycode.request.gioHangChiTIet.AddSoLuongReq;
import datn.be.mycode.request.gioHangChiTIet.GetAll;
import datn.be.mycode.service.GioHangChiTietService;
import datn.be.mycode.service.HoaDonChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/gio_hang_chi_tiet")
public class GioHangChiTietCotroller {

    @Autowired
    private GioHangChiTietService gioHangChiTietService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "idKhachHang",required = false) Long idKhachHang,
            @RequestParam(name = "status",required = false) Integer status
    ){

        GetAll request = new GetAll(page,pageSize,idKhachHang,status);
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        return ResponseEntity.ok(gioHangChiTietService.getAllByGioHang(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGioHangChiTiet(@RequestBody() AddGioHangChiTietReq request){
        return ResponseEntity.ok(gioHangChiTietService.addGioHangChiTiet(request));
    }

    @PostMapping("/add-so-luong")
    public ResponseEntity<?> addSoLuongGioHangChiTiet(@RequestBody() AddSoLuongReq request){
        return ResponseEntity.ok(gioHangChiTietService.addSoLuong(request));
    }

    @DeleteMapping("/delete-by-id/{idGioHangChiTiet}")
    public ResponseEntity<?> xoaGioHangChiTiet(@PathVariable("idGioHangChiTiet") Long idGioHangChiTiet){
        return ResponseEntity.ok(gioHangChiTietService.delete(idGioHangChiTiet));
    }

    @DeleteMapping("/delete-All/{idKhachHang}")
    public ResponseEntity<?> deleteAllGioHangChiTiet(@PathVariable("idKhachHang") Long idKhachHang){
        return ResponseEntity.ok(gioHangChiTietService.deleteAllByGioHang(idKhachHang));
    }

}
