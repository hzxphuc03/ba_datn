package datn.be.mycode.service;

import datn.be.mycode.entity.HopThoaiKhachHang;
import datn.be.mycode.entity.HopThoaiNhanVien;
import datn.be.mycode.repository.HopThoaiKhachHangRepository;
import datn.be.mycode.repository.HopThoaiNhanVienRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.request.NhanTin.KhachHang.HopThoai.AddHopThoaiKhachHang;
import datn.be.mycode.request.NhanTin.NhanVien.HopThoaiNhanVien.AddHopThoaiNhanVien;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HopThoaiKhachHangService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private HopThoaiKhachHangRepository hopThoaiKhachHangRepository;


    public ResponseEntity<?> addHopThoaiKhachHang(AddHopThoaiKhachHang request) {
        var khachHang = khachHangRepository.findById(request.getIdKhachHang()).orElse(null);
        var nhanVien = nhanVienRepository.findById(request.getIdNhanVien()).orElse(null);
        if(khachHang == null) return getErro("Không tìm thấy nhân viên có mã: "+ request.getIdKhachHang());
        if(nhanVien == null) return getErro("Không tìm thấy nhân viên có mã: "+ request.getIdNhanVien());

        HopThoaiKhachHang hopThoaiKhachHang = new HopThoaiKhachHang();

        hopThoaiKhachHang.setKhachHang(khachHang);
        hopThoaiKhachHang.setNhanVien(nhanVien);
        hopThoaiKhachHang.setTrangThai(1);

        return ResponseEntity.ok(hopThoaiKhachHangRepository.save(hopThoaiKhachHang));
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
