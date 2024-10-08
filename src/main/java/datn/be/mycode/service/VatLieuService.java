package datn.be.mycode.service;

import datn.be.mycode.entity.LoaiDe;
import datn.be.mycode.entity.VatLieu;
import datn.be.mycode.repository.LoaiDeRepository;
import datn.be.mycode.repository.VatLieuRepository;
import datn.be.mycode.request.LoaiCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.VatLieuRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class VatLieuService {

    @Autowired
    private VatLieuRepository repository;

    public NormalTableResponse<VatLieu> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<VatLieu> vatLieuNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<VatLieu> vatLieuPage;
        if (request.getKeyWord() != null) {
            vatLieuPage = repository.searchByTen(request.getKeyWord(), pageable);
        }else {
            vatLieuPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<VatLieu> vatLieus = vatLieuPage.getContent().stream().toList();

        vatLieuNormalTableResponse.setItem(vatLieus);
        vatLieuNormalTableResponse.setPage(request.getPage());
        vatLieuNormalTableResponse.setPageSize(request.getPageSize());
        vatLieuNormalTableResponse.setTotalItem(vatLieuPage.getTotalElements());

        return vatLieuNormalTableResponse;
    }

    public VatLieu add(String request) {

        VatLieu vatLieu = new VatLieu();
        vatLieu.setTen(request);
        vatLieu.setTrangThai(1);
        vatLieu.setNgayTao(LocalDateTime.now());
        return repository.save(vatLieu);
    }


    public VatLieu update(VatLieuRequest request) {
        VatLieu vatLieu = repository.findById(request.getId()).get();
        vatLieu.setTen(request.getTen());
        vatLieu.setTrangThai(request.getTrangThai());
        vatLieu.setNgaySua(LocalDateTime.now());
        return repository.save(vatLieu);
    }


    public VatLieu udateTrangThai(Long id, Integer status){
        VatLieu vatLieu = repository.findById(id).get();
        vatLieu.setTrangThai(status);
        return repository.save(vatLieu);
    }

}
