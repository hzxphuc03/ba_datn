package datn.be.mycode.service;

import datn.be.mycode.entity.ChiPhieuGiamGiaSuKien;
import datn.be.mycode.repository.ChiPhieuGiamGiaSuKienRepository;
import datn.be.mycode.repository.SuKienRepository;
import datn.be.mycode.repository.VoucherRepository;
import datn.be.mycode.request.ChiPhieuGGSKRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiPhieuGiamGiaSuKienService {
    @Autowired
    ChiPhieuGiamGiaSuKienRepository cprepository;
    @Autowired
    SuKienRepository suKienRepository;
    @Autowired
    VoucherRepository voucherRepository;

    public NormalTableResponse<ChiPhieuGiamGiaSuKien> getAll(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        NormalTableResponse<ChiPhieuGiamGiaSuKien> chiPhieuNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<ChiPhieuGiamGiaSuKien> chiPhieuGiamGiaSuKienPage;

        if (request.getKeyWord() != null) {
            chiPhieuGiamGiaSuKienPage = cprepository.findAllById(request.getKeyWord(), pageable);
        }else if (request.getStatus() != null){
            chiPhieuGiamGiaSuKienPage = cprepository.findAllByTrangThai(request.getStatus(), pageable);
        }else {
            chiPhieuGiamGiaSuKienPage = cprepository.findAll( pageable);
        }

        List<ChiPhieuGiamGiaSuKien> chiPhieuGiamGiaSuKiens = chiPhieuGiamGiaSuKienPage.getContent().stream().toList();

        chiPhieuNormalTableResponse.setItem(chiPhieuGiamGiaSuKiens);
        chiPhieuNormalTableResponse.setPage(request.getPage());
        chiPhieuNormalTableResponse.setPageSize(request.getPageSize());
        chiPhieuNormalTableResponse.setTotalItem(chiPhieuGiamGiaSuKienPage.getTotalElements());

        return chiPhieuNormalTableResponse;
    }


    public ChiPhieuGiamGiaSuKien add(ChiPhieuGGSKRequest request) {
        System.out.println(request);
        var voucher = voucherRepository.searchById(request.getVoucher()).get();
        var suKien = suKienRepository.searchById(request.getSuKien()).get();

        ChiPhieuGiamGiaSuKien cpggsk = new ChiPhieuGiamGiaSuKien();
        cpggsk.setKeys(request.getKeys());
        cpggsk.setSuKien(suKien);
        cpggsk.setVoucher(voucher);
        cpggsk.setTrangThai(1);
        return cprepository.save(cpggsk);
    }


    public ChiPhieuGiamGiaSuKien update(ChiPhieuGGSKRequest request) {
        ChiPhieuGiamGiaSuKien cpggsk = cprepository.findById(request.getId()).get();
        cpggsk.setKeys(request.getKeys());
        cpggsk.setSuKien(suKienRepository.searchById(request.getSuKien()).get());
        cpggsk.setVoucher(voucherRepository.searchById(request.getVoucher()).get());
        cpggsk.setTrangThai(request.getTrangThai());
        return cprepository.save(cpggsk);
    }

    public ChiPhieuGiamGiaSuKien udateTrangThai(Long id, Integer trangThai){
        ChiPhieuGiamGiaSuKien cpggsk = cprepository.findById(id).get();
        cpggsk.setTrangThai(trangThai);
        return cprepository.save(cpggsk);
    }
}
