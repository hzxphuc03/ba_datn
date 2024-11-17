package datn.be.mycode.service;

import datn.be.mycode.entity.DanhGiaSanPham;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.entity.HinhAnhComment;
import datn.be.mycode.entity.SanPham;
import datn.be.mycode.repository.DanhGiaSanPhamRepository;
import datn.be.mycode.repository.HoaDonChiTietRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.SanPhamRepository;
import datn.be.mycode.request.DanhGiaSanPhamRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.request.khachHang.DanhGiaSanPhamChungRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public DanhGiaSanPham udateTrangThai(Long id, Integer trangThai){
        DanhGiaSanPham danhGiaSanPham = danhGiaSanPhamRepository.findById(id).get();
        danhGiaSanPham.setTrangThai(trangThai);
        return danhGiaSanPhamRepository.save(danhGiaSanPham);
    }


private void updateTrungBinhDanhGia(Long idSanPham) {
    List<DanhGiaSanPham> danhGiaList = danhGiaSanPhamRepository.findByIdSanPham(idSanPham);

    double trungBinhSao = danhGiaList.stream()
            .mapToInt(DanhGiaSanPham::getSao)
            .average()
            .orElse(0);

    Optional<SanPham> sanPhamOptional = sanPhamRepository.findById(idSanPham);
    if (sanPhamOptional.isPresent()) {
        SanPham sanPham = sanPhamOptional.get();
        sanPham.setTrungBinhDanhGia((int) Math.round(trungBinhSao));
        sanPhamRepository.save(sanPham);
    }
}



    @Transactional
    public DanhGiaSanPham danhGiaTheoIDChiTietHoaDon(Long idChiTietHoaDon, DanhGiaSanPhamChungRequest request) {
    HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(idChiTietHoaDon).orElse(null);

    if (hoaDonChiTiet == null) {
        return null;
    }

    // Tạo đối tượng DanhGiaSanPham mới
    DanhGiaSanPham danhGiaSanPham = new DanhGiaSanPham();
    danhGiaSanPham.setSao(request.getSao());
    danhGiaSanPham.setCmt(request.getCmt());
    danhGiaSanPham.setNgayTao(LocalDateTime.now());
    danhGiaSanPham.setTrangThai(1);
    danhGiaSanPham.setIdKhachHang(hoaDonChiTiet.getHoaDon().getKhachHang());
    danhGiaSanPham.setIdSanPham(hoaDonChiTiet.getSanPhamChiTiet().getSanPham());

    // Xử lý danh sách hình ảnh từ request
    List<HinhAnhComment> hinhAnhList = request.getListHinhAnh().stream().map(hinhAnh -> {
        HinhAnhComment hinhAnhComment = new HinhAnhComment();
        hinhAnhComment.setAnh(hinhAnh);
        hinhAnhComment.setTrangThai(1);
        hinhAnhComment.setDanhGiaSanPham(danhGiaSanPham);
        return hinhAnhComment;
    }).collect(Collectors.toList());
    danhGiaSanPham.setHinhAnhComment(hinhAnhList);

    // Lưu đánh giá sản phẩm
    DanhGiaSanPham savedDanhGia = danhGiaSanPhamRepository.save(danhGiaSanPham);

    // Cập nhật trung bình đánh giá cho sản phẩm
    updateTrungBinhDanhGia(hoaDonChiTiet.getSanPhamChiTiet().getSanPham().getId());

    // Cập nhật trạng thái isCMT = true
    hoaDonChiTiet.setCMT(true);
    hoaDonChiTietRepository.save(hoaDonChiTiet);

    return savedDanhGia;
}

    @Transactional
    public List<DanhGiaSanPham> danhGiaChungByIDKH(Long idKhachHang, DanhGiaSanPhamChungRequest request) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepository
                .findAllByKhachHangIdAndIsCMTFalseAndIn7Days(idKhachHang, sevenDaysAgo);

        if (hoaDonChiTietList.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào cần đánh giá.");
        }

        // Tạo danh sách đánh giá sản phẩm
        List<DanhGiaSanPham> danhGiaList = hoaDonChiTietList.stream().map(chiTiet -> {
            DanhGiaSanPham danhGiaSanPham = new DanhGiaSanPham();
            danhGiaSanPham.setSao(request.getSao());
            danhGiaSanPham.setCmt(request.getCmt());
            danhGiaSanPham.setNgayTao(LocalDateTime.now());
            danhGiaSanPham.setTrangThai(1);
            danhGiaSanPham.setIdKhachHang(chiTiet.getHoaDon().getKhachHang());
            danhGiaSanPham.setIdSanPham(chiTiet.getSanPhamChiTiet().getSanPham());

            // Xử lý danh sách hình ảnh từ request
            List<HinhAnhComment> hinhAnhList = request.getListHinhAnh().stream().map(hinhAnh -> {
                HinhAnhComment hinhAnhComment = new HinhAnhComment();
                hinhAnhComment.setAnh(hinhAnh);
                hinhAnhComment.setTrangThai(1);
                hinhAnhComment.setDanhGiaSanPham(danhGiaSanPham);
                return hinhAnhComment;
            }).collect(Collectors.toList());
            danhGiaSanPham.setHinhAnhComment(hinhAnhList);

            return danhGiaSanPham;
        }).collect(Collectors.toList());

        // Lưu tất cả đánh giá sản phẩm
        List<DanhGiaSanPham> savedDanhGiaList = danhGiaSanPhamRepository.saveAll(danhGiaList);

        // Cập nhật trung bình đánh giá cho từng sản phẩm
        hoaDonChiTietList.forEach(chiTiet -> {
            updateTrungBinhDanhGia(chiTiet.getSanPhamChiTiet().getSanPham().getId());
        });

        // Cập nhật trạng thái isCMT = true cho các HoaDonChiTiet
        hoaDonChiTietList.forEach(chiTiet -> chiTiet.setCMT(true));
        hoaDonChiTietRepository.saveAll(hoaDonChiTietList);

        return savedDanhGiaList;
    }

    public NormalTableResponse<DanhGiaSanPham> getDanhGiaByKhachHangId(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<DanhGiaSanPham> danhGiaSanPhamNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<DanhGiaSanPham> danhGiaSanPhamPage;

        danhGiaSanPhamPage = danhGiaSanPhamRepository.findDanhGiaByKhachHangId(request.getKeyWord(), pageable);

        List<DanhGiaSanPham> lichSuSuDungs = danhGiaSanPhamPage.getContent().stream().toList();

        danhGiaSanPhamNormalTableResponse.setItem(lichSuSuDungs);
        danhGiaSanPhamNormalTableResponse.setPage(request.getPage());
        danhGiaSanPhamNormalTableResponse.setPageSize(request.getPageSize());
        danhGiaSanPhamNormalTableResponse.setTotalItem(danhGiaSanPhamPage.getTotalElements());

        return danhGiaSanPhamNormalTableResponse;
    }


    public NormalTableResponse<DanhGiaSanPham> getDanhGiaBySanPhamId(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<DanhGiaSanPham> danhGiaSanPhamNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<DanhGiaSanPham> danhGiaSanPhamPage;

        danhGiaSanPhamPage = danhGiaSanPhamRepository.findDanhGiaBySanPhamId(request.getKeyWord(), pageable);

        List<DanhGiaSanPham> lichSuSuDungs = danhGiaSanPhamPage.getContent().stream().toList();

        danhGiaSanPhamNormalTableResponse.setItem(lichSuSuDungs);
        danhGiaSanPhamNormalTableResponse.setPage(request.getPage());
        danhGiaSanPhamNormalTableResponse.setPageSize(request.getPageSize());
        danhGiaSanPhamNormalTableResponse.setTotalItem(danhGiaSanPhamPage.getTotalElements());

        return danhGiaSanPhamNormalTableResponse;
    }
}
