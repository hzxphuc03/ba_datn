package datn.be.mycode.service;

import datn.be.mycode.entity.DanhGiaSanPham;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.entity.HinhAnhComment;
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

    @Transactional
    public DanhGiaSanPham danhGiaTheoIDChiTietHoaDon(Long idChiTietHoaDon, DanhGiaSanPhamChungRequest request) {
        // Lấy HoaDonChiTiet từ idChiTietHoaDon, trả về null nếu không tìm thấy
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(idChiTietHoaDon).orElse(null);

        // Nếu không tìm thấy hoaDonChiTiet, trả về null hoặc có thể thay bằng giá trị mặc định
        if (hoaDonChiTiet == null) {
            return null; // Hoặc có thể trả về một giá trị mặc định tùy ý
        }

        // Tạo đối tượng DanhGiaSanPham mới
        DanhGiaSanPham danhGiaSanPham = new DanhGiaSanPham();
        danhGiaSanPham.setSao(request.getSao());
        danhGiaSanPham.setCmt(request.getCmt());
        danhGiaSanPham.setTrangThai(1);
        danhGiaSanPham.setIdKhachHang(hoaDonChiTiet.getHoaDon().getKhachHang());
        danhGiaSanPham.setIdSanPham(hoaDonChiTiet.getSanPhamChiTiet().getSanPham());

        // Xử lý danh sách hình ảnh từ request
        List<HinhAnhComment> hinhAnhList = request.getListHinhAnh().stream().map(hinhAnh -> {
            HinhAnhComment hinhAnhComment = new HinhAnhComment();
            hinhAnhComment.setAnh(hinhAnh);
            hinhAnhComment.setTrangThai(1);
            hinhAnhComment.setDanhGiaSanPham(danhGiaSanPham); // Liên kết với DanhGiaSanPham
            return hinhAnhComment;
        }).collect(Collectors.toList());
        danhGiaSanPham.setHinhAnhComment(hinhAnhList);

        // Lưu đánh giá sản phẩm và danh sách hình ảnh
        DanhGiaSanPham savedDanhGia = danhGiaSanPhamRepository.save(danhGiaSanPham);

        // Cập nhật trạng thái isCMT = true
        hoaDonChiTiet.setCMT(true);
        hoaDonChiTietRepository.save(hoaDonChiTiet);

        return savedDanhGia;
    }

    @Transactional
    public List<DanhGiaSanPham> danhGiaChungByIDKH(Long idKhachHang, DanhGiaSanPhamChungRequest request) {
        // Lấy thời gian 7 ngày trước từ hiện tại
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Lấy danh sách HoaDonChiTiet có isCMT = false và nằm trong 7 ngày
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
            danhGiaSanPham.setTrangThai(1);
            danhGiaSanPham.setIdKhachHang(chiTiet.getHoaDon().getKhachHang());
            danhGiaSanPham.setIdSanPham(chiTiet.getSanPhamChiTiet().getSanPham());

            // Xử lý danh sách hình ảnh từ request
            List<HinhAnhComment> hinhAnhList = request.getListHinhAnh().stream().map(hinhAnh -> {
                HinhAnhComment hinhAnhComment = new HinhAnhComment();
                hinhAnhComment.setAnh(hinhAnh);
                hinhAnhComment.setTrangThai(1);
                hinhAnhComment.setDanhGiaSanPham(danhGiaSanPham); // Liên kết với DanhGiaSanPham
                return hinhAnhComment;
            }).collect(Collectors.toList());
            danhGiaSanPham.setHinhAnhComment(hinhAnhList);

            return danhGiaSanPham;
        }).collect(Collectors.toList());

        // Lưu tất cả đánh giá sản phẩm
        List<DanhGiaSanPham> savedDanhGiaList = danhGiaSanPhamRepository.saveAll(danhGiaList);

        // Cập nhật trạng thái isCMT = true cho các HoaDonChiTiet
        hoaDonChiTietList.forEach(chiTiet -> chiTiet.setCMT(true));
        hoaDonChiTietRepository.saveAll(hoaDonChiTietList);

        return savedDanhGiaList;
    }
    public Page<DanhGiaSanPham> getDanhGiaByKhachHangId(Long idKhachHang, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return danhGiaSanPhamRepository.findDanhGiaByKhachHangId(idKhachHang, pageable);
    }

}
