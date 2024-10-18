package datn.be.mycode.service;

import datn.be.mycode.entity.SanPham;
import datn.be.mycode.entity.SanPhamChiTiet;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.SanPhamChiTietRequest;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SanPhamChiTietResponse;
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
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAll( request, pageable);
        }else {
            sanPhamChiTietResponsePage = sanPhamChiTietRepository.searchAllSortedBySoLuong( request, pageable);
        }
        List<SanPhamChiTietResponse> sanPhamChiTietResponses = sanPhamChiTietResponsePage.getContent().stream().toList();

        sanPhamNormalTableResponse.setItem(sanPhamChiTietResponses);
        sanPhamNormalTableResponse.setPage(request.getPage());
        sanPhamNormalTableResponse.setPageSize(request.getPageSize());
        sanPhamNormalTableResponse.setTotalItem(sanPhamChiTietResponsePage.getTotalElements());

        return sanPhamNormalTableResponse;
    }

    public SanPhamChiTietByIdSanPhamRes getByIdSanPham(Long idSanPham){

        List<SanPhamChiTiet> listSanPhamChiTiet = sanPhamChiTietRepository.findAllBySanPham_Id(idSanPham);

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


        return sanPhamChiTietR;
    }

    public SanPhamChiTiet update(SanPhamChiTietRequest request){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(request.getId()).get();
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


        SanPhamChiTiet sanPhamChiTietR = sanPhamChiTietRepository.save(sanPhamChiTiet);

        boolean check = false;
        if(sanPham.getGiaThapNhat().compareTo(sanPhamChiTietR.getGiaBan())>0){
            sanPham.setGiaThapNhat(sanPhamChiTietR.getGiaBan());
            check = true;
        }else if(sanPham.getGiaThapNhat().intValue() == 0){
            sanPham.setGiaThapNhat(sanPhamChiTietR.getGiaBan());
            check = true;
        }
        if(sanPham.getGiaCaoNhat().compareTo(sanPhamChiTietR.getGiaBan())<0){
            sanPham.setGiaCaoNhat(sanPhamChiTietR.getGiaBan());
            check = true;
        }
        if(sanPhamChiTietR.getTrangThai() == 0){
            sanPham.setSoLuong(sanPham.getSoLuong() - sanPhamChiTiet.getSoLuong());
            check = true;
        }
        if(sanPhamChiTietR.getSoLuong() != sanPhamChiTiet.getSoLuong()){
            sanPham.setSoLuong(sanPham.getSoLuong() - sanPhamChiTiet.getSoLuong() + sanPhamChiTietR.getSoLuong());
            check = true;
        }
        if(check) {
            sanPhamRepository.save(sanPham);
        }

        return sanPhamChiTietR;
    }

    public SanPhamChiTiet udateTrangThai(Long id, Integer status){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
        SanPham sanPham = sanPhamRepository.findById(sanPhamChiTiet.getSanPham().getId()).get();

        sanPhamChiTiet.setTrangThai(status);

        SanPhamChiTiet sanPhamChiTietR = sanPhamChiTietRepository.save(sanPhamChiTiet);

        if(sanPhamChiTietR.getTrangThai() == 0){
            sanPham.setSoLuong(sanPham.getSoLuong() - sanPhamChiTiet.getSoLuong());
        } else{
            sanPham.setSoLuong(sanPham.getSoLuong() + sanPhamChiTiet.getSoLuong());
        }
        sanPhamRepository.save(sanPham);

        return sanPhamChiTietR;
    }
}
