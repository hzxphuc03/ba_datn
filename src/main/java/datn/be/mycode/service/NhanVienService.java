package datn.be.mycode.service;

import datn.be.mycode.entity.NhanVien;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.request.NhanVienRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository repo;

    public NormalTableResponse<NhanVien> getAll(NormalTableRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<NhanVien> nhanvienNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<NhanVien> nhanvienPage;
        if (request.getKeyWord() != null) {
            nhanvienPage = repo.findByHoTen(request.getKeyWord(), pageable);
        }else {
            nhanvienPage = repo.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<NhanVien> nhanviens = nhanvienPage.getContent().stream().toList();

        nhanvienNormalTableResponse.setItem(nhanviens);
        nhanvienNormalTableResponse.setPage(request.getPage());
        nhanvienNormalTableResponse.setPageSize(request.getPageSize());
        nhanvienNormalTableResponse.setTotalItem(nhanvienPage.getTotalElements());

        return nhanvienNormalTableResponse;
    }


    public NhanVien add(String request) {

        System.out.println(request);
        NhanVien nhanvien = new NhanVien();
        nhanvien.setMa(request);
        nhanvien.setHoTen(request);
        nhanvien.setNgaySinh(LocalDateTime.now());
        nhanvien.setNgayTao(LocalDateTime.now());
        nhanvien.setSoDienThoai(request);
        nhanvien.setGioiTinh(request);
        nhanvien.setDiaChi(request);
        nhanvien.setHinhAnh(request);
        nhanvien.setEmail(request);
        nhanvien.setTaiKhoan(request);
        nhanvien.setMatKhau(request);
        nhanvien.setTrangThai(1);
        return repo.save(nhanvien);
    }


    public NhanVien update(NhanVienRequest request) {
        NhanVien nhanvien = repo.findById(request.getId()).get();
        nhanvien.setMa(request.getMa());
        nhanvien.setHoTen(request.getHoten());
        nhanvien.setNgaySinh(LocalDateTime.now());
        nhanvien.setNgayTao(LocalDateTime.now());
        nhanvien.setSoDienThoai(request.getSoDienThoai());
        nhanvien.setGioiTinh(request.getGioiTinh());
        nhanvien.setDiaChi(request.getDiaChi());
        nhanvien.setHinhAnh(request.getHinhAnh());
        nhanvien.setEmail(request.getEmail());
        nhanvien.setTaiKhoan(request.getTaiKhoan());
        nhanvien.setMatKhau(request.getMatKhau());
        nhanvien.setTrangThai(request.getTrangThai());

        return repo.save(nhanvien);
    }


    public void delete(NhanVien nhanvien) {
        repo.delete(nhanvien);
    }


    public NhanVien deleteById(Long id) {
        NhanVien nhanvien = repo.findById(id).get();
        nhanvien.setTrangThai(0);
        return repo.save(nhanvien);
    }

    public NhanVien udateTrangThai(Long id, Integer status){
        NhanVien nhanvien = repo.findById(id).get();
        nhanvien.setTrangThai(status);
        return repo.save(nhanvien);
    }
}
