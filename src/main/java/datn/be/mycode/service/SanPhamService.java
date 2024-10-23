package datn.be.mycode.service;


import datn.be.mycode.entity.SanPham;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.SanPhamRequest;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository repo;
    @Autowired
    private GiamGiaRepository giamGiaRepository;
    @Autowired
    private ThuongHieuRepository thuongHieuRepository;
    @Autowired
    private TheLoaiRepository theLoaiRepository;
    @Autowired
    private LoaiCoRepository loaiCoRepository;
    @Autowired
    private VatLieuRepository vatLieuRepository;
    @Autowired
    private LoaiDeRepository loaiDeRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

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
            sanPhamResponsePage = repo.searchAll(request, pageable);
        }else {
            sanPhamResponsePage = repo.searchAllSortedBySoLuong(request, pageable);
        }
        List<SanPhamResponse> sanPhamResponses = sanPhamResponsePage.getContent().stream().toList();

        sanPhamNormalTableResponse.setItem(sanPhamResponses);
        sanPhamNormalTableResponse.setPage(request.getPage());
        sanPhamNormalTableResponse.setPageSize(request.getPageSize());
        sanPhamNormalTableResponse.setTotalItem(sanPhamResponsePage.getTotalElements());

        return sanPhamNormalTableResponse;
    }


    public SanPham add(SanPhamRequest request) {
        SanPham sanPham = SanPham.builder()
                .ten(request.getTen())
                .moTa(request.getMoTa())
                .hinhAnh(request.getHinhAnh())
                .trongLuong(request.getTrongLuong())
                .beMatSuDung(request.getBeMatSuDung())
                .congNghe(request.getCongNghe())
                .kieuDang(request.getKieuDang())
                .giaThapNhat(BigDecimal.valueOf(0))
                .giaCaoNhat(BigDecimal.valueOf(0))
                .ngayTao(LocalDateTime.now())
                .nguoiTao(request.getNguoiTao())
                .soLuong(0)
                .trangThai(1)
                .giamGia(giamGiaRepository.findById(request.getIdGiamGia()).orElse(null))
                .thuongHieu(thuongHieuRepository.findById(request.getIdThuongHieu()).get())
                .theLoai(theLoaiRepository.findById(request.getIdTheLoai()).get())
                .loaiCo(loaiCoRepository.findById(request.getIdLoaiCo()).get())
                .vatLieu(vatLieuRepository.findById(request.getIdVatLieu()).get())
                .loaiDe(loaiDeRepository.findById(request.getIdLoaiDe()).get())
                .build();

        return repo.save(sanPham);
    }


    public SanPham update(SanPhamRequest request) {
        SanPham sanPham = repo.findById(request.getId()).get();

        sanPham.setMa(request.getMa());
        sanPham.setTen(request.getTen());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setHinhAnh(request.getHinhAnh());
        sanPham.setTrongLuong(request.getTrongLuong());
        sanPham.setBeMatSuDung(request.getBeMatSuDung());
        sanPham.setCongNghe(request.getCongNghe());
        sanPham.setKieuDang(request.getKieuDang());
        sanPham.setNgaySua(LocalDateTime.now());
        sanPham.setTrangThai(request.getTrangThai());
        sanPham.setGiamGia(giamGiaRepository.findById(request.getIdGiamGia()).orElse(null));
        sanPham.setThuongHieu(thuongHieuRepository.findById(request.getIdThuongHieu()).get());
        sanPham.setTheLoai(theLoaiRepository.findById(request.getIdTheLoai()).get());
        sanPham.setLoaiCo(loaiCoRepository.findById(request.getIdLoaiCo()).get());
        sanPham.setVatLieu(vatLieuRepository.findById(request.getIdVatLieu()).get());
        sanPham.setLoaiDe(loaiDeRepository.findById(request.getIdLoaiDe()).get());
        return repo.save(sanPham);
    }


//    public void delete(SanPham sanPham) {
//        repo.delete(sanPham);
//    }

    public SanPhamResponse getSanPhamById(Long id){
        return repo.searchById(id);
    }

    public SanPham udateTrangThai(Long id, Integer status){
        SanPham sanPham = repo.findById(id).get();
        sanPham.setTrangThai(status);
        return repo.save(sanPham);
    }

    public void update(SanPham sanPham) {
        repo.save(sanPham);
    }
}
