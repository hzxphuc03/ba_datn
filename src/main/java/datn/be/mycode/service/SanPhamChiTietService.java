package datn.be.mycode.service;

import datn.be.mycode.entity.SanPham;
import datn.be.mycode.entity.SanPhamChiTiet;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.SanPhamChiTietRequest;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SanPhamChiTietResponse;
import datn.be.mycode.response.customResponse.SanPhamChiTiet.SanPhamBanHang;
import datn.be.mycode.response.customResponse.SanPhamChiTiet.SanPhamChiTietByIdSP;
import datn.be.mycode.response.customResponse.SanPhamChiTiet.SanPhamChiTietByIdSanPhamRes;
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
    @Autowired
    private KichcoRepository kichcoRepository;


    public NormalTableResponse<SanPhamChiTietResponse> getAll(TableSanPhamChiTietRequest request) {

        CheckRequest(request);

        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SanPhamChiTietResponse> sanPhamChiTietResponsePage;
        if (request.getSort() == 0) {
            System.out.println(request.toString());
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAll( request, pageable);
        }else {
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAllSortedBySoLuong( request, pageable);
        }
        List<SanPhamChiTietResponse> sanPhamChiTietResponses = sanPhamChiTietResponsePage.getContent().stream().toList();

        var sanPhamNormalTableResponse = new NormalTableResponse<>(sanPhamChiTietResponses,request.getPage(),request.getPageSize(),sanPhamChiTietResponsePage.getTotalElements());

        return sanPhamNormalTableResponse;
    }

    public SanPhamChiTietByIdSanPhamRes getByIdSanPham(Long idSanPham){

        List<SanPhamChiTiet> listSanPhamChiTiet = sanPhamChiTietRepository.findAllBySanPham_Id(idSanPham);

        List<SanPhamChiTietByIdSP> listSanPhamChiTietRes = getSanPhamChiTietsBySanPham(listSanPhamChiTiet);

        SanPham sanPhamRes = listSanPhamChiTiet.get(0).getSanPham();

        SanPhamChiTietByIdSanPhamRes sanPhamChiTietByIdSanPhamRes = new SanPhamChiTietByIdSanPhamRes(sanPhamRes, listSanPhamChiTietRes);
        return sanPhamChiTietByIdSanPhamRes;
    }

    public SanPhamChiTiet addSPCT(SanPhamChiTietRequest request){
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
        SanPham sanPham = sanPhamRepository.findById(request.getSanPham()).get();

        sanPhamChiTiet.setNgayTao(LocalDateTime.now());
        sanPhamChiTiet.setHinhAnh(request.getHinhAnh());
        sanPhamChiTiet.setGiaNhap(request.getGiaNhap());
        sanPhamChiTiet.setGiaBan(request.getGiaBan());
        sanPhamChiTiet.setSoLuong(request.getSoLuong());
        sanPhamChiTiet.setTrangThai(request.getTrangThai());
        sanPhamChiTiet.setSanPham(sanPham);
        sanPhamChiTiet.setMauSac(mauSacRepository.findById(request.getMauSac()).get());
        sanPhamChiTiet.setKichCo(kichcoRepository.findById(request.getKichCo()).get());

        SanPhamChiTiet sanPhamChiTietR = sanPhamChiTietRepository.save(sanPhamChiTiet);

        CheckPriceMinAndMax(sanPham, sanPhamChiTietR);

        return sanPhamChiTietR;
    }

    private void CheckPriceMinAndMax(SanPham sanPham, SanPhamChiTiet sanPhamChiTietR) {
        if(sanPham.getGiaThapNhat().compareTo(sanPhamChiTietR.getGiaBan())>0){
            sanPham.setGiaThapNhat(sanPhamChiTietR.getGiaBan());
        }else if(sanPham.getGiaThapNhat().intValue() == 0){
            sanPham.setGiaThapNhat(sanPhamChiTietR.getGiaBan());
        }
        if(sanPham.getGiaCaoNhat().compareTo(sanPhamChiTietR.getGiaBan())<0){
            sanPham.setGiaCaoNhat(sanPhamChiTietR.getGiaBan());
        }
        sanPham.setSoLuong(sanPham.getSoLuong() + sanPhamChiTietR.getSoLuong());
        sanPhamRepository.save(sanPham);
    }

    public SanPhamChiTiet update(SanPhamChiTietRequest request){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(request.getId()).get();
        int trangThai = sanPhamChiTiet.getTrangThai();
        int soLuong = sanPhamChiTiet.getSoLuong();
        SanPham sanPham = sanPhamRepository.findById(request.getSanPham()).get();

        sanPhamChiTiet.setNgaySua(LocalDateTime.now());
        sanPhamChiTiet.setHinhAnh(request.getHinhAnh());
        sanPhamChiTiet.setGiaNhap(request.getGiaNhap());
        sanPhamChiTiet.setGiaBan(request.getGiaBan());
        sanPhamChiTiet.setSoLuong(request.getSoLuong());
        sanPhamChiTiet.setTrangThai(request.getTrangThai());
        sanPhamChiTiet.setSanPham(sanPham);
        sanPhamChiTiet.setMauSac(mauSacRepository.findById(request.getMauSac()).get());
        sanPhamChiTiet.setKichCo(kichcoRepository.findById(request.getKichCo()).get());


        sanPhamChiTiet = sanPhamChiTietRepository.save(sanPhamChiTiet);

        boolean check = false;
        if(sanPham.getGiaThapNhat().compareTo(sanPhamChiTiet.getGiaBan())>0){
            sanPham.setGiaThapNhat(sanPhamChiTiet.getGiaBan());
            check = true;
        }else if(sanPham.getGiaThapNhat().intValue() == 0){
            sanPham.setGiaThapNhat(sanPhamChiTiet.getGiaBan());
            check = true;
        }

        if(sanPham.getGiaCaoNhat().compareTo(sanPhamChiTiet.getGiaBan())<0){
            sanPham.setGiaCaoNhat(sanPhamChiTiet.getGiaBan());
            check = true;
        }
        if(trangThai != sanPhamChiTiet.getTrangThai()) {
            check = true;

            if (sanPhamChiTiet.getTrangThai() == 0) {
                sanPham.setSoLuong(sanPham.getSoLuong() - soLuong);
            }else {
                sanPham.setSoLuong(sanPham.getSoLuong() + sanPhamChiTiet.getSoLuong());
            }
        } else if( trangThai ==1){
            if (soLuong != sanPhamChiTiet.getSoLuong()) {
                    sanPham.setSoLuong(sanPham.getSoLuong() + sanPhamChiTiet.getSoLuong() - soLuong);
                    check = true;
                }
        }
        if(check) {
            sanPhamRepository.save(sanPham);
        }

        return sanPhamChiTiet;
    }

    public SanPhamChiTiet udateTrangThai(Long id, Integer status){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
        int trangThai = sanPhamChiTiet.getTrangThai();
        int soLuong = sanPhamChiTiet.getSoLuong();
        SanPham sanPham = sanPhamRepository.findById(sanPhamChiTiet.getSanPham().getId()).get();

        sanPhamChiTiet.setTrangThai(status);

        sanPhamChiTiet = sanPhamChiTietRepository.save(sanPhamChiTiet);

        if(sanPhamChiTiet.getTrangThai()!= trangThai){
            if(sanPhamChiTiet.getTrangThai() == 0){
            sanPham.setSoLuong(sanPham.getSoLuong() - sanPhamChiTiet.getSoLuong());
            } else{
            sanPham.setSoLuong(sanPham.getSoLuong() + sanPhamChiTiet.getSoLuong());
            }
        }
        sanPhamRepository.save(sanPham);

        return sanPhamChiTiet;
    }

    public NormalTableResponse<SanPhamBanHang> getAllSanPham(TableSanPhamChiTietRequest request) {

        CheckRequest(request);

        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SanPhamChiTietResponse> sanPhamChiTietResponsePage;

        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(pageable);
        List<SanPham> sanPhams = sanPhamPage.getContent();
        List<SanPhamBanHang> sanPhamBanHangs = new ArrayList<>();
        if (sanPhams!=null) {
            for (SanPham sanPham : sanPhams) {
                SanPhamBanHang sanPhamBanHang = new SanPhamBanHang();
                sanPhamBanHang.setSanPham(sanPham);
                sanPhamBanHang.setSanPhamChiTiets(sanPhamChiTietRepository.findAllBySanPham_Id(sanPham.getId()));
                sanPhamBanHangs.add(sanPhamBanHang);
            }
        }

        var sanPhamNormalTableResponse = new NormalTableResponse<>(sanPhamBanHangs,request.getPage(),request.getPageSize(),sanPhamPage.getTotalElements());


        return sanPhamNormalTableResponse;

    }

    private void CheckRequest(TableSanPhamChiTietRequest request) {
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
    }

    List<SanPhamChiTietByIdSP> getSanPhamChiTietsBySanPham(List<SanPhamChiTiet> listSanPhamChiTiet){

        List<SanPhamChiTietByIdSP> listSanPhamChiTietRes = new ArrayList<>();

        listSanPhamChiTiet.forEach(sanPhamChiTiet -> {
            SanPhamChiTietByIdSP sanPhamChiTietByIdSP = SanPhamChiTietByIdSP.builder()
                    .id(sanPhamChiTiet.getId())
                    .ngayTao(sanPhamChiTiet.getNgayTao())
                    .ngaySua(sanPhamChiTiet.getNgaySua())
                    .hinhAnh(sanPhamChiTiet.getHinhAnh())
                    .giaNhap(sanPhamChiTiet.getGiaNhap())
                    .giaBan(sanPhamChiTiet.getGiaBan())
                    .soLuong(sanPhamChiTiet.getSoLuong())
                    .trangThai(sanPhamChiTiet.getTrangThai())
                    .sanPham(sanPhamChiTiet.getSanPham().getId())
                    .mauSac(sanPhamChiTiet.getMauSac())
                    .kichCo(sanPhamChiTiet.getKichCo())
                    .build();
            listSanPhamChiTietRes.add(sanPhamChiTietByIdSP);
        });

        return listSanPhamChiTietRes;
    }
}
