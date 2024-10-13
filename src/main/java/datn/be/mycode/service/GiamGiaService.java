package datn.be.mycode.service;

import datn.be.mycode.entity.GiamGia;
import datn.be.mycode.entity.KichCo;
import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.repository.GiamGiaRepository;
import datn.be.mycode.repository.KichcoRepository;
import datn.be.mycode.request.GiamGiaRequest;
import datn.be.mycode.request.KichCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.TheLoaiRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiamGiaService {
    @Autowired
    private GiamGiaRepository repository;

    public NormalTableResponse<GiamGia> getAll(NormalTableRequest request){
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<GiamGia> GiamGiaNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable= PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<GiamGia> Page;
        if (request.getKeyWord() != null) {
            Page = repository.findByTen(request.getKeyWord(), pageable);
        }else {
           Page = repository.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<GiamGia> giamGias = Page.getContent().stream().toList();

        GiamGiaNormalTableResponse.setItem(giamGias);
        GiamGiaNormalTableResponse.setPage(request.getPage());
        GiamGiaNormalTableResponse.setPageSize(request.getPageSize());
        GiamGiaNormalTableResponse.setTotalItem(Page.getTotalElements());
        return GiamGiaNormalTableResponse;

    }

    public GiamGia getById(Long id){

        return repository.findById(id).get();
    }

    public GiamGia add(GiamGiaRequest request){
        GiamGia giamGia= new GiamGia();
        giamGia.setTen(request.getTen());
        giamGia.setNgayBatDau(request.getNgayBatDau());
        giamGia.setNgayKetThuc(request.getNgayKetThuc());
        giamGia.setMucGiam(request.getMucGiam());
        giamGia.setTrangThai(1);
        giamGia.setNgayTao(LocalDateTime.now());

        return repository.save(giamGia);
    }

    public GiamGia update(GiamGiaRequest request) {
        GiamGia giamGia = repository.findById(request.getId()).get();
        giamGia.setTen(request.getTen());
        giamGia.setNgayBatDau(request.getNgayBatDau());
        giamGia.setNgayKetThuc(request.getNgayKetThuc());
        giamGia.setMucGiam(request.getMucGiam());
        giamGia.setTrangThai(request.getTrangThai());
        giamGia.setNgaySua(LocalDateTime.now());
        return repository.save(giamGia);
    }


    public void delete(GiamGia giamGia) {
        repository.delete(giamGia);
    }


    public GiamGia deleteById(Long id) {
        GiamGia giamGia = repository.findById(id).get();
        giamGia.setTrangThai(0);
        return repository.save(giamGia);
    }

    public GiamGia udateTrangThai(Long id, Integer status){
       GiamGia giamGia = repository.findById(id).get();
       giamGia.setTrangThai(status);
        return repository.save(giamGia);
    }
}
