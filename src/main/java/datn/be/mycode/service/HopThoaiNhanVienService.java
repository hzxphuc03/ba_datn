package datn.be.mycode.service;

import datn.be.mycode.entity.HopThoaiNhanVien;
import datn.be.mycode.repository.HopThoaiNhanVienRepository;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.request.NhanTin.NhanVien.HopThoaiNhanVien.AddHopThoai;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HopThoaiNhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private HopThoaiNhanVienRepository hopThoaiNhanVienRepository;

    public ResponseEntity<?> addHopThoaiNhanVien(AddHopThoai request){
        var nhanVien1 = nhanVienRepository.findById(request.getIdNhanVien1()).orElse(null);
        var nhanVien2 = nhanVienRepository.findById(request.getIdNhanVien2()).orElse(null);
        if(nhanVien1 == null) return getErro("Không tìm thấy nhân viên có mã: "+ request.getIdNhanVien1());
        if(nhanVien2 == null) return getErro("Không tìm thấy nhân viên có mã: "+ request.getIdNhanVien2());

        HopThoaiNhanVien hopThoaiNhanVien = new HopThoaiNhanVien();

        hopThoaiNhanVien.setNhanVien1(nhanVien1);
        hopThoaiNhanVien.setNhanVien2(nhanVien2);
        hopThoaiNhanVien.setTrangThai(1);

        return ResponseEntity.ok(hopThoaiNhanVienRepository.save(hopThoaiNhanVien));
    }



    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
