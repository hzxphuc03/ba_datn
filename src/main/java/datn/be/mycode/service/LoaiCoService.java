package datn.be.mycode.service;

import datn.be.mycode.entity.LoaiCo;
import datn.be.mycode.repository.LoaiCoRepository;
import datn.be.mycode.request.LoaiCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class LoaiCoService {

    @Autowired
    private LoaiCoRepository repository;

    public NormalTableResponse<LoaiCo> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<LoaiCo> loaiCoNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<LoaiCo> loaiCoPage;
        if (request.getKeyWord() != null) {
            loaiCoPage = repository.searchByTen(request.getKeyWord(), pageable);
        }else {
            loaiCoPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<LoaiCo> loaiCos = loaiCoPage.getContent().stream().toList();

        loaiCoNormalTableResponse.setItem(loaiCos);
        loaiCoNormalTableResponse.setPage(request.getPage());
        loaiCoNormalTableResponse.setPageSize(request.getPageSize());
        loaiCoNormalTableResponse.setTotalItem(loaiCoPage.getTotalElements());

        return loaiCoNormalTableResponse;
    }

    public LoaiCo add(String request) {

        LoaiCo loaiCo = new LoaiCo();
        loaiCo.setTen(request);
        loaiCo.setTrangThai(1);
        loaiCo.setNgayTao(LocalDateTime.now());
        return repository.save(loaiCo);
    }


    public LoaiCo update(LoaiCoRequest request) {
        LoaiCo loaiCo = repository.findById(request.getId()).get();
        loaiCo.setTen(request.getTen());
        loaiCo.setTrangThai(request.getTrangThai());
        loaiCo.setNgaySua(LocalDateTime.now());
        return repository.save(loaiCo);
    }


    public LoaiCo udateTrangThai(Long id, Integer status){
        LoaiCo loaiCo = repository.findById(id).get();
        loaiCo.setTrangThai(status);
        return repository.save(loaiCo);
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAllByTrangThai(1));
    }
}
