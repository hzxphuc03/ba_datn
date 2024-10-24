package datn.be.mycode.service;

import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.entity.NhanVien;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.request.KhachHangRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.NormalTableRequestKhachHang;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KhachHangService {
    @Autowired
    private KhachHangRepository repository;

    public NormalTableResponse<KhachHang> getAll(NormalTableRequestKhachHang request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }


        NormalTableResponse<KhachHang> khachHangNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<KhachHang> khachHangPage;
        if (request.getKeyWord() != null) {
            khachHangPage = repository.searchByTen(request.getKeyWord(), pageable);
            if(khachHangPage.getTotalElements() == 0){
                khachHangPage = repository.searchBySoDienThoai(request.getKeyWord(), pageable);
                if(khachHangPage.getTotalElements() == 0){
                    khachHangPage = repository.searchByTaiKhoan(request.getKeyWord(), pageable);
                    if(khachHangPage.getTotalElements() == 0){
                        khachHangPage = repository.searchByEmail(request.getKeyWord(), pageable);
                    }
                }
            }
        } else if (request.getStatus() != null){
            khachHangPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }else{
            khachHangPage = repository.findAll(pageable);
        }

        List<KhachHang> khachHangs = khachHangPage.getContent().stream().toList();

        khachHangNormalTableResponse.setItem(khachHangs);
        khachHangNormalTableResponse.setPage(request.getPage());
        khachHangNormalTableResponse.setPageSize(request.getPageSize());
        khachHangNormalTableResponse.setTotalItem(khachHangPage.getTotalElements());

        return khachHangNormalTableResponse;
    }

    public KhachHang getById(Long id){
        return repository.findById(id).get();
    }

    public KhachHang add(KhachHangRequest request) {
        System.out.println("addKH1");
        System.out.println(request);
        KhachHang khachHang = new KhachHang();
        khachHang.setMa(request.getMa());
        khachHang.setHoTen(request.getHoTen());
        khachHang.setHinhAnh(request.getHinhAnh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(1);
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setEmail(request.getEmail());
        khachHang.setTaiKhoan(request.getTaiKhoan());
        khachHang.setMatKhau(request.getMatKhau());

        return repository.save(khachHang);
    }




    public KhachHang update(KhachHangRequest request) {
        KhachHang khachHang = repository.findById(request.getId()).get();
        khachHang.setMa(request.getMa());
        khachHang.setHoTen(request.getHoTen());
        khachHang.setHinhAnh(request.getHinhAnh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(request.getTrangThai());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setEmail(request.getEmail());
        khachHang.setTaiKhoan(request.getTaiKhoan());
        khachHang.setMatKhau(request.getMatKhau());
        return repository.save(khachHang);
    }


//    public void delete(KhachHang khachHang) {
//        repository.delete(khachHang);
//    }
//
//
//    public KhachHang deleteById(Long id) {
//        KhachHang khachHang = repository.findById(id).get();
//        khachHang.setTrangThai(0);
//        return repository.save(khachHang);
//    }

    public KhachHang udateTrangThai(Long id, Integer status){
        KhachHang khachHang = repository.findById(id).get();
        khachHang.setTrangThai(status);
        return repository.save(khachHang);
    }
}
