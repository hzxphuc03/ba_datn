package datn.be.mycode.service;

import datn.be.mycode.entity.GiamGia;
import datn.be.mycode.entity.KichCo;
import datn.be.mycode.repository.GiamGiaRepository;
import datn.be.mycode.repository.KichcoRepository;
import datn.be.mycode.request.GiamGiaRequest;
import datn.be.mycode.request.KichCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiamGiaService {
    @Autowired
    private GiamGiaRepository repository;

    public NormalTableResponse<GiamGia> getALL(NormalTableRequest request){
        NormalTableResponse<GiamGia> GiamGiaNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable= PageRequest.of(request.getPage(),request.getPageSize());
        Page<GiamGia> Page;
        if (!request.getKeyWord().equals("")) {
            Page = repository.findByTen(request.getKeyWord(), pageable);
        }else {
           Page = repository.findAllByTrangThai(request.getStatus(), pageable);
        }
        List<GiamGia> giamGias = Page.getContent().stream().toList();

        GiamGiaNormalTableResponse .setItem(giamGias);
        GiamGiaNormalTableResponse .getPage();
        GiamGiaNormalTableResponse.getPageSize();
        GiamGiaNormalTableResponse.setTotalItem(Page.getTotalElements());
        return GiamGiaNormalTableResponse;

    }
    public  GiamGia add(GiamGiaRequest request){
        GiamGia giamGia= new GiamGia();
        giamGia.setTen(request.getTen());
        giamGia.setTrangThai(1);
        giamGia.setNgayTao(LocalDateTime.now());

        return repository.save(giamGia);
    }
}
