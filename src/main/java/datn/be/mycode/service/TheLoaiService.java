package datn.be.mycode.service;

import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.repository.TheLoaiRepository;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.TheLoaiRequest;
import datn.be.mycode.request.TrangThaiRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TheLoaiService {

    @Autowired
    private TheLoaiRepository repo;

    public NormalTableResponse<TheLoai> getAll(NormalTableRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<TheLoai> theLoaiNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<TheLoai> theLoaiPage;
        if (request.getKeyWord() != null) {
            theLoaiPage = repo.findByTen(request.getKeyWord(), pageable);
        }else {
            theLoaiPage = repo.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<TheLoai> theLoais = theLoaiPage.getContent().stream().toList();

        theLoaiNormalTableResponse.setItem(theLoais);
        theLoaiNormalTableResponse.setPage(request.getPage());
        theLoaiNormalTableResponse.setPageSize(request.getPageSize());
        theLoaiNormalTableResponse.setTotalItem(theLoaiPage.getTotalElements());

        return theLoaiNormalTableResponse;
    }


    public TheLoai add(String request) {
        System.out.println("adsfsdf");
        System.out.println(request);
        TheLoai theLoai = new TheLoai();
        theLoai.setTen(request);
        theLoai.setTrangThai(1);
        theLoai.setNgayTao(LocalDateTime.now());
        return repo.save(theLoai);
    }


    public TheLoai update(TheLoaiRequest request) {
        TheLoai theLoai = repo.findById(request.getId()).get();
        theLoai.setTen(request.getTen());
        theLoai.setTrangThai(request.getTrangThai());
        theLoai.setNgaySua(LocalDateTime.now());
        return repo.save(theLoai);
    }


    public void delete(TheLoai theLoai) {
        repo.delete(theLoai);
    }


    public TheLoai deleteById(Long id) {
        TheLoai theLoai = repo.findById(id).get();
        theLoai.setTrangThai(0);
        return repo.save(theLoai);
    }

    public TheLoai udateTrangThai(Long id, Integer status){
        TheLoai theLoai = repo.findById(id).get();
        theLoai.setTrangThai(status);
        return repo.save(theLoai);
    }
}
