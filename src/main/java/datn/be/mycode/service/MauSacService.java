package datn.be.mycode.service;


import datn.be.mycode.entity.MauSac;
import datn.be.mycode.entity.ThuongHieu;
import datn.be.mycode.repository.MauSacRepository;
import datn.be.mycode.repository.ThuongHieuRepository;
import datn.be.mycode.request.MauSacRequest;
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
public class MauSacService {

    @Autowired
    private MauSacRepository repository;

    public NormalTableResponse<MauSac> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<MauSac> mauSacNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<MauSac> mauSacPage;
        if (request.getKeyWord() != null) {
            mauSacPage = repository.searchByTen(request.getKeyWord(), pageable);
        }else {
            mauSacPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<MauSac> mauSacs = mauSacPage.getContent().stream().toList();

        mauSacNormalTableResponse.setItem(mauSacs);
        mauSacNormalTableResponse.setPage(request.getPage());
        mauSacNormalTableResponse.setPageSize(request.getPageSize());
        mauSacNormalTableResponse.setTotalItem(mauSacPage.getTotalElements());

        return mauSacNormalTableResponse;
    }

    public MauSac add(String request) {
        System.out.println("addso1");
        System.out.println(request);
        MauSac mauSac = new MauSac();
        mauSac.setTen(request);
        mauSac.setMaMau(request);
        mauSac.setTrangThai(1);
        mauSac.setNgayTao(LocalDateTime.now());
        return repository.save(mauSac);
    }


    public MauSac update(MauSacRequest request) {
        MauSac mauSac = repository.findById(request.getId()).get();
        mauSac.setTen(request.getTen());
        mauSac.setMaMau(request.getMaMau());
        mauSac.setTrangThai(request.getTrangThai());
        mauSac.setNgaySua(LocalDateTime.now());
        return repository.save(mauSac);
    }


//    public void delete(MauSac mauSac) {
//        repository.delete(mauSac);
//    }
//
//
//    public MauSac deleteById(Long id) {
//        MauSac mauSac = repository.findById(id).get();
//        mauSac.setTrangThai(0);
//        return repository.save(mauSac);
//    }

    public MauSac udateTrangThai(Long id, Integer status){
        MauSac mauSac = repository.findById(id).get();
        mauSac.setTrangThai(status);
        return repository.save(mauSac);
    }
}
