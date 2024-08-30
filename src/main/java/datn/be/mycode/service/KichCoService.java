package datn.be.mycode.service;

import datn.be.mycode.entity.KichCo;
import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.repository.KichcoRepository;
import datn.be.mycode.request.KichCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.response.NormalTableResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KichCoService {
    @Autowired
    private KichcoRepository repository;

    public NormalTableResponse<KichCo> getALL(NormalTableRequest request){
        NormalTableResponse<KichCo> kichCoNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable= PageRequest.of(request.getPage(),request.getPageSize());
        Page<KichCo> kichCoPage;
        if (!request.getKeyWord().equals("")) {
            kichCoPage = repository.findByTen(request.getKeyWord(), pageable);
        }else {
            kichCoPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<KichCo> kichCos = kichCoPage.getContent().stream().toList();

        kichCoNormalTableResponse .setItem(kichCos);
        kichCoNormalTableResponse .getPage();
        kichCoNormalTableResponse .getPageSize();
        kichCoNormalTableResponse.setTotalItem(kichCoPage.getTotalElements());
        return kichCoNormalTableResponse;

    }
    public  KichCo add(KichCoRequest request){
        KichCo kichCo= new KichCo();
        kichCo.setTen(request.getTen());
        kichCo.setTrangThai(1);
        kichCo.setNgayTao(LocalDateTime.now());

    return repository.save(kichCo);
    }
}