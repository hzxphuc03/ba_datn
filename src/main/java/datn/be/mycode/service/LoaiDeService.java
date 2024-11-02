package datn.be.mycode.service;

import datn.be.mycode.entity.LoaiCo;
import datn.be.mycode.entity.LoaiDe;
import datn.be.mycode.repository.LoaiCoRepository;
import datn.be.mycode.repository.LoaiDeRepository;
import datn.be.mycode.request.LoaiCoRequest;
import datn.be.mycode.request.LoaiDeRequest;
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
public class LoaiDeService {

    @Autowired
    private LoaiDeRepository repository;

    public NormalTableResponse<LoaiDe> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<LoaiDe> loaiDeNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<LoaiDe> loaiDePage;
        if (request.getKeyWord() != null) {
            loaiDePage = repository.searchByTen(request.getKeyWord(), pageable);
        }else {
            loaiDePage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<LoaiDe> loaiDes = loaiDePage.getContent().stream().toList();

        loaiDeNormalTableResponse.setItem(loaiDes);
        loaiDeNormalTableResponse.setPage(request.getPage());
        loaiDeNormalTableResponse.setPageSize(request.getPageSize());
        loaiDeNormalTableResponse.setTotalItem(loaiDePage.getTotalElements());

        return loaiDeNormalTableResponse;
    }

    public LoaiDe add(String request) {

        LoaiDe loaiDe = new LoaiDe();
        loaiDe.setTen(request);
        loaiDe.setTrangThai(1);
        loaiDe.setNgayTao(LocalDateTime.now());
        return repository.save(loaiDe);
    }


    public LoaiDe update(LoaiDeRequest request) {
        LoaiDe loaiDe = repository.findById(request.getId()).get();
        loaiDe.setTen(request.getTen());
        loaiDe.setTrangThai(request.getTrangThai());
        loaiDe.setNgaySua(LocalDateTime.now());
        return repository.save(loaiDe);
    }


    public LoaiDe udateTrangThai(Long id, Integer status){
        LoaiDe loaiDe = repository.findById(id).get();
        loaiDe.setTrangThai(status);
        return repository.save(loaiDe);
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAllByTrangThai(1));
    }
}
