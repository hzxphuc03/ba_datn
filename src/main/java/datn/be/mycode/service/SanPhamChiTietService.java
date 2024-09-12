package datn.be.mycode.service;

import datn.be.mycode.entity.SanPham;
import datn.be.mycode.entity.SanPhamChiTiet;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.SanPhamChiTietRequest;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SanPhamChiTietResponse;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
//    @Autowired
//    private SanPhamRepository sanPhamRepositoryrepo;
//    @Autowired
//    private SanPhamRepository sanPhamRepositoryrepo;
    @Autowired
    private ThuongHieuRepository thuongHieuRepository;
    @Autowired
    private TheLoaiRepository theLoaiRepository;

    public NormalTableResponse<SanPhamChiTietResponse> getAll(TableSanPhamChiTietRequest request) {
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
        NormalTableResponse<SanPhamChiTietResponse> sanPhamNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SanPhamChiTietResponse> sanPhamChiTietResponsePage;
        if (request.getSort() == 0) {
            System.out.println(request.toString());
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAll( request.getStartDate(), request.getEndDate(), request.getStatus(), request.getSanPham(), request.getMauSac(), request.getKichCo(), request.getThuongHieu(), request.getTheLoai(), pageable);
        }else {
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAllSortedBySoLuong( request.getStartDate(), request.getEndDate(), request.getStatus(), request.getTheLoai(), request.getTheLoai(), request.getTheLoai(), request.getTheLoai(), request.getTheLoai(), pageable);
        }
        List<SanPhamChiTietResponse> sanPhamChiTietResponses = sanPhamChiTietResponsePage.getContent().stream().toList();

        sanPhamNormalTableResponse.setItem(sanPhamChiTietResponses);
        sanPhamNormalTableResponse.setPage(request.getPage());
        sanPhamNormalTableResponse.setPageSize(request.getPageSize());
        sanPhamNormalTableResponse.setTotalItem(sanPhamChiTietResponsePage.getTotalElements());

        return sanPhamNormalTableResponse;
    }


    public SanPhamChiTiet addSPCT(SanPhamChiTietRequest request){
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
        sanPhamChiTiet.setNgayTao(LocalDateTime.now());
        sanPhamChiTiet.setHinhAnh(request.getHinhAnh());
        sanPhamChiTiet.setGiaHienHanh(request.getGiaHienHanh());
        sanPhamChiTiet.setSoLuong(request.getSoLuong());
        sanPhamChiTiet.setTrangThai(request.getTrangThai());
        sanPhamChiTiet.setSanPham(sanPhamRepository.findById(request.getSanPham()).get());
        sanPhamChiTiet.setMauSac(mauSacRepository.findById(request.getMauSac()).get());
//        sanPhamChiTiet.setSanPham(sanPhamRepository.findById(request.getSanPham()).get());
//        sanPhamChiTiet.setSanPham(sanPhamRepository.findById(request.getSanPham()).get());
        sanPhamChiTiet.setThuongHieu(thuongHieuRepository.findById(request.getThuongHieu()).get());
        sanPhamChiTiet.setTheLoai(theLoaiRepository.findById(request.getTheLoai()).get());

        return sanPhamChiTietRepository.save(sanPhamChiTiet);
    }
}
