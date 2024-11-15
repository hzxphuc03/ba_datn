package datn.be.mycode.service;

import datn.be.mycode.entity.DanhGiaSanPham;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.repository.DanhGiaSanPhamRepository;
import datn.be.mycode.repository.HoaDonChiTietRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.SanPhamRepository;
import datn.be.mycode.request.DanhGiaSanPhamRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DanhGiaSanPhamService {
    @Autowired
    DanhGiaSanPhamRepository danhGiaSanPhamRepository;
     @Autowired
     SanPhamRepository sanPhamRepository;
     @Autowired
     KhachHangRepository khachHangRepository;
     @Autowired
     private HoaDonChiTietRepository hoaDonChiTietRepository;

    public NormalTableResponse<DanhGiaSanPham> getAll(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        NormalTableResponse<DanhGiaSanPham> danhGiaSanPhamNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<DanhGiaSanPham> danhGiaSanPhamPage;

        if (request.getKeyWord() != null) {
            danhGiaSanPhamPage = danhGiaSanPhamRepository.findAllById(request.getKeyWord(), pageable);
        }else if (request.getStatus() != null){
            danhGiaSanPhamPage = danhGiaSanPhamRepository.findAllByTrangThai(request.getStatus(), pageable);
        }else {
            danhGiaSanPhamPage = danhGiaSanPhamRepository.findAll( pageable);
        }

        List<DanhGiaSanPham> lichSuSuDungs = danhGiaSanPhamPage.getContent().stream().toList();

        danhGiaSanPhamNormalTableResponse.setItem(lichSuSuDungs);
        danhGiaSanPhamNormalTableResponse.setPage(request.getPage());
        danhGiaSanPhamNormalTableResponse.setPageSize(request.getPageSize());
        danhGiaSanPhamNormalTableResponse.setTotalItem(danhGiaSanPhamPage.getTotalElements());

        return danhGiaSanPhamNormalTableResponse;
    }

    @Transactional
    public DanhGiaSanPham add(DanhGiaSanPhamRequest request) {

        // Tạo mới một đánh giá sản phẩm
        DanhGiaSanPham danhGiaSanPham = new DanhGiaSanPham();
        danhGiaSanPham.setSao(request.getSao());
        danhGiaSanPham.setCmt(request.getCmt());
        danhGiaSanPham.setListHinhAnh(request.getListHinhAnh());
        danhGiaSanPham.setIdKhachHang(khachHangRepository.searchById(request.getIdKhachHang()).get());
        danhGiaSanPham.setIdSanPham(sanPhamRepository.timTheoId(request.getIdSanPham()).get());
        danhGiaSanPham.setTrangThai(1);

        // Lưu đánh giá vào cơ sở dữ liệu
        danhGiaSanPham = danhGiaSanPhamRepository.save(danhGiaSanPham);

        // Cập nhật isCMT thành true cho các bản ghi HoaDonChiTiet tương ứng
        hoaDonChiTietRepository.updateIsCMTByKhachHangAndSanPham(request.getIdKhachHang(), request.getIdSanPham());

        return danhGiaSanPham;
    }
    @Transactional
    public List<DanhGiaSanPham> addAllDanhGia(Long idKhachHang, int sao, String cmt, String listHinhAnh) {
        KhachHang khachHang = khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));

        // Tìm tất cả các sản phẩm mà khách hàng chưa đánh giá (isCMT = false)
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepository.timKhachHangIdCoIsCMTLaFalse(idKhachHang);

        List<DanhGiaSanPham> danhGiaList = new ArrayList<>();

        // Tạo đánh giá cho từng sản phẩm và lưu vào danh sách
        for (HoaDonChiTiet hdc : hoaDonChiTietList) {
            DanhGiaSanPham danhGiaSanPham = new DanhGiaSanPham();
            danhGiaSanPham.setSao(sao);
            danhGiaSanPham.setCmt(cmt);
            danhGiaSanPham.setListHinhAnh(listHinhAnh);
            danhGiaSanPham.setIdKhachHang(khachHang);
            danhGiaSanPham.setIdSanPham(hdc.getSanPhamChiTiet().getSanPham());
            danhGiaSanPham.setTrangThai(1);

            // Lưu từng đánh giá vào cơ sở dữ liệu
            danhGiaSanPham = danhGiaSanPhamRepository.save(danhGiaSanPham);
            danhGiaList.add(danhGiaSanPham);
        }

        // Cập nhật tất cả các bản ghi HoaDonChiTiet thành isCMT = true
        hoaDonChiTietRepository.updateIsCMTThanhTrueTuKhachHangId(idKhachHang);

        return danhGiaList;
    }

    public DanhGiaSanPham update(DanhGiaSanPhamRequest request) {
        DanhGiaSanPham danhGiaSanPham = danhGiaSanPhamRepository.findById(request.getId()).get();
        danhGiaSanPham.setSao(request.getSao());
        danhGiaSanPham.setCmt(request.getCmt());
        danhGiaSanPham.setListHinhAnh(request.getListHinhAnh());
        danhGiaSanPham.setIdKhachHang(khachHangRepository.searchById(request.getIdKhachHang()).get());
        danhGiaSanPham.setIdSanPham(sanPhamRepository.timTheoId(request.getIdSanPham()).get());
        danhGiaSanPham.setTrangThai(request.getTrangThai());
        return danhGiaSanPhamRepository.save(danhGiaSanPham);
    }

    public DanhGiaSanPham udateTrangThai(Long id, Integer trangThai){
        DanhGiaSanPham danhGiaSanPham = danhGiaSanPhamRepository.findById(id).get();
        danhGiaSanPham.setTrangThai(trangThai);
        return danhGiaSanPhamRepository.save(danhGiaSanPham);
    }
}
