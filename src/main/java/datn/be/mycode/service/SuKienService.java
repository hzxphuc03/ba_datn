package datn.be.mycode.service;

import datn.be.mycode.entity.SuKien;
import datn.be.mycode.repository.SuKienRepository;
import datn.be.mycode.request.SuKienRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuKienService {
    @Autowired
    SuKienRepository suKienRepository;

    public NormalTableResponse<SuKien> getAll(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        NormalTableResponse<SuKien> suKienNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SuKien> suKienPage;

        if (request.getKeyWord() != null) {
            suKienPage = suKienRepository.findAllById(request.getKeyWord(), pageable);
        } else if (request.getStatus() != null){
            suKienPage = suKienRepository.findAllByTrangThai(request.getStatus(), pageable);
        }else {
            suKienPage = suKienRepository.findAll( pageable);
        }

        List<SuKien> suKiens = suKienPage.getContent().stream().toList();

        suKienNormalTableResponse.setItem(suKiens);
        suKienNormalTableResponse.setPage(request.getPage());
        suKienNormalTableResponse.setPageSize(request.getPageSize());
        suKienNormalTableResponse.setTotalItem(suKienPage.getTotalElements());

        return suKienNormalTableResponse;
    }


    public SuKien add(SuKienRequest request) {
        System.out.println(request);
        SuKien suKien = new SuKien();
        suKien.setTenSuKien(request.getTenSuKien());
        suKien.setMaSuKien(request.getMaSuKien());
        suKien.setNgayBatDau(request.getNgayBatDau());
        suKien.setNgayKetThuc(request.getNgayKetThuc());
        suKien.setKeySuKien(request.getKeySuKien());
        suKien.setTrangThai(1);
        return suKienRepository.save(suKien);
    }


    public SuKien update(SuKienRequest request) {
        SuKien suKien = suKienRepository.findById(request.getId()).get();
        suKien.setTenSuKien(request.getTenSuKien());
        suKien.setMaSuKien(request.getMaSuKien());
        suKien.setNgayBatDau(request.getNgayBatDau());
        suKien.setNgayKetThuc(request.getNgayKetThuc());
        suKien.setKeySuKien(request.getKeySuKien());
        suKien.setTrangThai(request.getTrangThai());
        return suKienRepository.save(suKien);
    }

    public SuKien udateTrangThai(Long id, Integer trangThai){
        SuKien suKien = suKienRepository.findById(id).get();
        suKien.setTrangThai(trangThai);
        return suKienRepository.save(suKien);
    }

}
