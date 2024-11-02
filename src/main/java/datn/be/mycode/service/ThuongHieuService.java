package datn.be.mycode.service;

import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.entity.ThuongHieu;
import datn.be.mycode.repository.ThuongHieuRepository;

import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.ThuongHieuRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ThuongHieuService {

    @Autowired
    private ThuongHieuRepository repository;

    public NormalTableResponse<ThuongHieu> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<ThuongHieu> thuongHieuNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<ThuongHieu> thuongHieuPage;
        if (request.getKeyWord() != null) {
            thuongHieuPage = repository.searchByTen(request.getKeyWord(), pageable);
        }else {
            thuongHieuPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<ThuongHieu> thuongHieus = thuongHieuPage.getContent().stream().toList();

        thuongHieuNormalTableResponse.setItem(thuongHieus);
        thuongHieuNormalTableResponse.setPage(request.getPage());
        thuongHieuNormalTableResponse.setPageSize(request.getPageSize());
        thuongHieuNormalTableResponse.setTotalItem(thuongHieuPage.getTotalElements());

        return thuongHieuNormalTableResponse;
    }

    public ThuongHieu add(String request) {
        System.out.println("addso1");
        System.out.println(request);
        ThuongHieu thuongHieu = new ThuongHieu();
        thuongHieu.setTen(request);
        thuongHieu.setTrangThai(1);
        thuongHieu.setNgayTao(LocalDateTime.now());
        return repository.save(thuongHieu);
    }


    public ThuongHieu update(ThuongHieuRequest request) {
        ThuongHieu thuongHieu = repository.findById(request.getId()).get();
        thuongHieu.setTen(request.getTen());
        thuongHieu.setTrangThai(request.getTrangThai());
        thuongHieu.setNgaySua(LocalDateTime.now());
        return repository.save(thuongHieu);
    }


//    public void delete(ThuongHieu thuongHieu) {
//        repository.delete(thuongHieu);
//    }
//
//
//    public ThuongHieu deleteById(Long id) {
//        ThuongHieu thuongHieu = repository.findById(id).get();
//        thuongHieu.setTrangThai(0);
//        return repository.save(thuongHieu);
//    }

    public ThuongHieu udateTrangThai(Long id, Integer status){
        ThuongHieu thuongHieu = repository.findById(id).get();
        thuongHieu.setTrangThai(status);
        return repository.save(thuongHieu);
    }
    public List<ThuongHieu> findAllThuongHieu() {
        return repository.findAllByTrangThai(1);
    }

}
