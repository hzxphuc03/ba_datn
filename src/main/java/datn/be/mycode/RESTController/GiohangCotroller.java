package datn.be.mycode.RESTController;

import datn.be.mycode.request.GioHang.GioHangAdd;
import datn.be.mycode.request.HoaDon.HuyHoaDonRequest;
import datn.be.mycode.request.HoaDon.NextStep;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.customRequest.BanHang.*;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.service.GioHangService;
import datn.be.mycode.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/gio_hang")
public class GiohangCotroller {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping("/{idKhachHang}")
    public ResponseEntity<?> getAll(@PathVariable("idKhachHang") Long idKhachHang){
        return ResponseEntity.ok(gioHangService.getByKhachHang(idKhachHang).getBody());
    }

    @GetMapping("/get_so_luong/{idKhachHang}")
    public ResponseEntity<?> getSoLuong(@PathVariable("idKhachHang") Long idKhachHang){
        return ResponseEntity.ok(gioHangService.getSoLuong(idKhachHang).getBody());
    }

    @PutMapping("/update")
    public ResponseEntity<?> addGioHang(@RequestBody() GioHangAdd request){
        ResponseEntity<?> responseEntity = gioHangService.updateGioHang(request);
        return responseEntity;
    }

}
