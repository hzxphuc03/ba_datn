package datn.be.mycode.service;

import datn.be.mycode.entity.ChucVu;
import datn.be.mycode.repository.ChucVuRepository;
import datn.be.mycode.request.ChucVuRequest;
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
public class ChucVuService {

    @Autowired
    private ChucVuRepository repo;

    public NormalTableResponse<ChucVu> getAll(NormalTableRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<ChucVu> theLoaiNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<ChucVu> chucVuPage;
        if (request.getKeyWord() != null) {
            chucVuPage = repo.searchByTen(request.getKeyWord(), pageable);
        }else {
            chucVuPage = repo.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<ChucVu> chucVus = chucVuPage.getContent();

        theLoaiNormalTableResponse.setItem(chucVus);
        theLoaiNormalTableResponse.setPage(request.getPage());
        theLoaiNormalTableResponse.setPageSize(request.getPageSize());
        theLoaiNormalTableResponse.setTotalItem(chucVuPage.getTotalElements());

        return theLoaiNormalTableResponse;
    }


    public ChucVu add(String request) {
        ChucVu chucVu = new ChucVu();
        chucVu.setTen(request);
        chucVu.setTrangThai(1);
        chucVu.setNgayTao(LocalDateTime.now());
        return repo.save(chucVu);
    }


    public ChucVu update(ChucVuRequest request) {
        ChucVu chucVu = repo.findById(request.getId()).get();
        chucVu.setTen(request.getTen());
        chucVu.setTrangThai(request.getTrangThai());
        chucVu.setNgaySua(LocalDateTime.now());
        return repo.save(chucVu);
    }


    public ChucVu udateTrangThai(Long id, Integer status){
        ChucVu chucVu = repo.findById(id).get();
        chucVu.setTrangThai(status);
        return repo.save(chucVu);
    }
}
