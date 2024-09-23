package datn.be.mycode.service;

import datn.be.mycode.entity.DiaChi;
import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.repository.DiaChiRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.request.DiaChiRequest;
import datn.be.mycode.request.KhachHangRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaChiService {

    @Autowired
    private DiaChiRepository repository;

    @Autowired
    private KhachHangRepository khRepo;

    public NormalTableResponse<DiaChi> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<DiaChi> diaChiNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<DiaChi> diaChiPage;
        if (request.getKeyWord() != null) {
            diaChiPage = repository.searchByDiaChi(request.getKeyWord(), pageable);
        }else {
            diaChiPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<DiaChi> diaChis = diaChiPage.getContent().stream().toList();

        diaChiNormalTableResponse.setItem(diaChis);
        diaChiNormalTableResponse.setPage(request.getPage());
        diaChiNormalTableResponse.setPageSize(request.getPageSize());
        diaChiNormalTableResponse.setTotalItem(diaChiPage.getTotalElements());

        return diaChiNormalTableResponse;
    }

    public DiaChi add(DiaChiRequest request) {
        System.out.println("addDC1");
        System.out.println(request);
        DiaChi diaChi = new DiaChi();
        diaChi.setDiaChi(request.getDiaChi());
        diaChi.setTrangThai(1);
        diaChi.setKhachHang(khRepo.searchById(request.getIdKhachHang()).get());
        return repository.save(diaChi);
    }

    public DiaChi update(DiaChiRequest request) {
        DiaChi diaChi = repository.findById(request.getId()).get();
        diaChi.setDiaChi(request.getDiaChi());
        diaChi.setKhachHang(khRepo.searchById(request.getIdKhachHang()).get());
        diaChi.setTrangThai(request.getTrangThai());
        return repository.save(diaChi);
    }

    public DiaChi udateTrangThai(Long id, Integer status){
        DiaChi diaChi = repository.findById(id).get();
        diaChi.setTrangThai(status);
        return repository.save(diaChi);
    }
}
