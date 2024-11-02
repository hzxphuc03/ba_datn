package datn.be.mycode.service;

import datn.be.mycode.entity.LichSuSuDung;
import datn.be.mycode.repository.ChiPhieuGiamGiaSuKienRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.LichSuSuDungRepository;
import datn.be.mycode.request.LichSuSuDungRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LichSuSuDungService {
    @Autowired
    LichSuSuDungRepository repository;
    @Autowired
    ChiPhieuGiamGiaSuKienRepository chiPhieuRepository;
    @Autowired
    KhachHangRepository khachHangRepository;


    public NormalTableResponse<LichSuSuDung> getAll(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        NormalTableResponse<LichSuSuDung> lichSuSuDungNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<LichSuSuDung> lichSuSuDungPage;

        if (request.getKeyWord() != null) {
            lichSuSuDungPage = repository.findAllById(request.getKeyWord(), pageable);
        }else if (request.getStatus() != null){
            lichSuSuDungPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }else {
            lichSuSuDungPage = repository.findAll( pageable);
        }

        List<LichSuSuDung> lichSuSuDungs = lichSuSuDungPage.getContent().stream().toList();

        lichSuSuDungNormalTableResponse.setItem(lichSuSuDungs);
        lichSuSuDungNormalTableResponse.setPage(request.getPage());
        lichSuSuDungNormalTableResponse.setPageSize(request.getPageSize());
        lichSuSuDungNormalTableResponse.setTotalItem(lichSuSuDungPage.getTotalElements());

        return lichSuSuDungNormalTableResponse;
    }


    public LichSuSuDung add(LichSuSuDungRequest request) {
        System.out.println(request);
        LichSuSuDung lichSuSuDung = new LichSuSuDung();
        lichSuSuDung.setNgayTao(LocalDateTime.now());
        lichSuSuDung.setKeys(request.getKeys());
        lichSuSuDung.setChiPhieuGiamGiaSuKien(chiPhieuRepository.searchById(request.getChiPhieuGiamGiaSuKien()).get());
        lichSuSuDung.setKhachHang(khachHangRepository.searchById(request.getKhachHang()).get());
        lichSuSuDung.setTrangThai(1);
        return repository.save(lichSuSuDung);
    }


    public LichSuSuDung update(LichSuSuDungRequest request) {
        LichSuSuDung lichSuSuDung = repository.findById(request.getId()).get();
        lichSuSuDung.setKeys(request.getKeys());
        lichSuSuDung.setChiPhieuGiamGiaSuKien(chiPhieuRepository.searchById(request.getChiPhieuGiamGiaSuKien()).get());
        lichSuSuDung.setKhachHang(khachHangRepository.searchById(request.getKhachHang()).get());
        lichSuSuDung.setTrangThai(request.getTrangThai());
        return repository.save(lichSuSuDung);
    }

    public LichSuSuDung udateTrangThai(Long id, Integer trangThai){
        LichSuSuDung lichSuSuDung = repository.findById(id).get();
        lichSuSuDung.setTrangThai(trangThai);
        return repository.save(lichSuSuDung);
    }
}
