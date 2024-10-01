package datn.be.mycode.service;


import datn.be.mycode.entity.SanPham;
import datn.be.mycode.repository.SanPhamRepository;
import datn.be.mycode.request.SanPhamRequest;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository repo;

    public NormalTableResponse<SanPhamResponse> getAll(TableSanPhamRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        if (request.getSort() == null) {
            request.setSort(0);
        }
        NormalTableResponse<SanPhamResponse> sanPhamNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SanPhamResponse> sanPhamResponsePage;
        if (request.getSort() == 0) {
            sanPhamResponsePage = repo.searchAll(request.getKeyWord(), request.getStartDate(), request.getEndDate(), request.getStatus(), pageable);
        }else {
            sanPhamResponsePage = repo.searchAllSortedBySoLuong(request.getKeyWord(), request.getStartDate(), request.getEndDate(), request.getStatus(), pageable);
        }
        List<SanPhamResponse> sanPhamResponses = sanPhamResponsePage.getContent().stream().toList();

        sanPhamNormalTableResponse.setItem(sanPhamResponses);
        sanPhamNormalTableResponse.setPage(request.getPage());
        sanPhamNormalTableResponse.setPageSize(request.getPageSize());
        sanPhamNormalTableResponse.setTotalItem(sanPhamResponsePage.getTotalElements());

        return sanPhamNormalTableResponse;
    }


    public SanPham add(SanPhamRequest request) {
        SanPham sanPham = new SanPham();
//        sanPham.setMa(request.getMa());
        sanPham.setTen(request.getTen());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setHinhAnh(request.getHinhAnh());
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setTrangThai(1);
        return repo.save(sanPham);
    }


    public SanPham update(SanPhamRequest request) {
        SanPham sanPham = repo.findById(request.getId()).get();
        sanPham.setMa(request.getMa());
        sanPham.setTen(request.getTen());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setHinhAnh(request.getHinhAnh());
        sanPham.setNgaySua(LocalDateTime.now());
        sanPham.setTrangThai(request.getTrangThai());
        return repo.save(sanPham);
    }


//    public void delete(SanPham sanPham) {
//        repo.delete(sanPham);
//    }


    public SanPham udateTrangThai(Long id, Integer status){
        SanPham sanPham = repo.findById(id).get();
        sanPham.setTrangThai(status);
        return repo.save(sanPham);
    }
}
