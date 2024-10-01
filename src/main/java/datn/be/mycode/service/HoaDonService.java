package datn.be.mycode.service;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.SanPham;
import datn.be.mycode.entity.ThaoTacHoaDon;
import datn.be.mycode.repository.HoaDonRepository;
import datn.be.mycode.repository.KhachHangRepository;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.repository.PhuongThucThanhToanRepository;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.HoaDonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository repo;
    @Autowired
    private ThaoTacHoaDonService thaoTacHoaDonService;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    public NormalTableResponse<HoaDonResponse> getAll(TableHoaDonRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<HoaDonResponse> hoaDonNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<HoaDonResponse> hoaDonResponsePage;
        hoaDonResponsePage = repo.searchAll(request.getKeyWord(), request.getStartDate(), request.getEndDate(), request.getIdKhachHang(), request.getIdNhanVien(), pageable);
        List<HoaDonResponse> hoaDonResponses = hoaDonResponsePage.getContent().stream().toList();

        hoaDonNormalTableResponse.setItem(hoaDonResponses);
        hoaDonNormalTableResponse.setPage(request.getPage());
        hoaDonNormalTableResponse.setPageSize(request.getPageSize());
        hoaDonNormalTableResponse.setTotalItem(hoaDonResponsePage.getTotalElements());

        return hoaDonNormalTableResponse;
    }

    public HoaDon add(HoaDonRequest request){

        HoaDon hoaDon = new HoaDon().builder()
                .ma(request.getMa())
                .ngayTao(request.getNgayTao())
                .ngayThanhToan(request.getNgayThanhToan())
                .ghiChu(request.getGhiChu())
                .tenNguoiNhan(request.getTenNguoiNhan())
                .sdtNguoiNhan(request.getSdtNguoiNhan())
                .diaChiNguoiNhan(request.getDiaChiNguoiNhan())
                .phiShip(request.getPhiShip())
                .tongTien(request.getTongTien())
                .tongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam())
                .tongTienGiam(request.getTongTienGiam())
                .phuongThucThanhToan(phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).get())
                .nhanVien(nhanVienRepository.findById(request.getIdNhanVien()).get())
                .khachHang(khachHangRepository.findById(request.getIdKhachHang()).get())
                .build();

        HoaDon hoaDonRes = repo.save(hoaDon);
        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon().builder()
                .thaoTac("add")
                .trangThai(1)
                .hoaDon(hoaDonRes)
                .nhanVien(hoaDon.getNhanVien())
                .build();

        thaoTacHoaDonService.add(thaoTacHoaDon);

        return hoaDonRes;
    }

    public HoaDon update(HoaDonRequest request){

        HoaDon hoaDon = new HoaDon().builder()
                .ma(request.getMa())
                .ngayThanhToan(request.getNgayThanhToan())
                .ghiChu(request.getGhiChu())
                .tenNguoiNhan(request.getTenNguoiNhan())
                .sdtNguoiNhan(request.getSdtNguoiNhan())
                .diaChiNguoiNhan(request.getDiaChiNguoiNhan())
                .phiShip(request.getPhiShip())
                .tongTien(request.getTongTien())
                .tongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam())
                .tongTienGiam(request.getTongTienGiam())
                .phuongThucThanhToan(phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).get())
                .nhanVien(nhanVienRepository.findById(request.getIdNhanVien()).get())
                .khachHang(khachHangRepository.findById(request.getIdKhachHang()).get())
                .build();

        HoaDon hoaDonRes = repo.save(hoaDon);
        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon().builder()
                .thaoTac("update")
                .trangThai(1)
                .hoaDon(hoaDonRes)
                .nhanVien(hoaDon.getNhanVien())
                .build();

        thaoTacHoaDonService.add(thaoTacHoaDon);

        return hoaDonRes;
    }

    public HoaDon udateTrangThai(Long id, Integer status){
        HoaDon hoaDon = repo.findById(id).get();
        hoaDon.setTrangThai(status);

        HoaDon hoaDonRes = repo.save(hoaDon);
        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon().builder()
                .thaoTac("delete")
                .trangThai(1)
                .hoaDon(hoaDonRes)
                .nhanVien(hoaDon.getNhanVien())
                .build();

        thaoTacHoaDonService.add(thaoTacHoaDon);

        return hoaDonRes;
    }
}
