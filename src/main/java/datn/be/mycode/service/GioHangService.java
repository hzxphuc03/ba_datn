package datn.be.mycode.service;

import datn.be.mycode.repository.GioHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    public ResponseEntity<?> getByKhachHang(Long idKhachHang) {
        var gioHang = gioHangRepository.findByKhachHangId(idKhachHang).orElse(null);
//        if (gioHang == null )
        return ResponseEntity.ok(gioHang);
    }
}
