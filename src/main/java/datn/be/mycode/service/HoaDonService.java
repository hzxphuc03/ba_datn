package datn.be.mycode.service;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.NhanVien;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public NormalTableResponse<HoaDon> getAll(TableHoaDonRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<HoaDon> hoaDonNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<HoaDon> hoaDonResponsePage;
        hoaDonResponsePage = repo.searchAll(request.getKeyWord(), request.getStartDate(), request.getEndDate(), request.getIdKhachHang(), request.getIdNhanVien(), pageable);
        List<HoaDon> hoaDonResponses = hoaDonResponsePage.getContent().stream().toList();

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
    public HoaDon add(Long idNhanVien){
        NhanVien nhanVien = nhanVienRepository.findById(idNhanVien).orElse(null);
if(nhanVien != null){
        HoaDon hoaDon = new HoaDon().builder()
                .ngayTao(LocalDateTime.now())
                .phiShip(BigDecimal.valueOf(0))
                .tongTien(BigDecimal.valueOf(0))
                .tongTienSanPhamChuaGiam(BigDecimal.valueOf(0))
                .tongTienGiam(BigDecimal.valueOf(0))
                .phuongThucThanhToan(phuongThucThanhToanRepository.findById(Long.valueOf(1)).get())
                .nhanVien(nhanVien)
                .trangThai(1)
                .build();

        HoaDon hoaDonRes = repo.save(hoaDon);
        hoaDonRes.setMa("HD" + hoaDonRes.getId());
        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon().builder()
                .thaoTac("add")
                .trangThai(1)
                .hoaDon(hoaDonRes)
                .nhanVien(hoaDon.getNhanVien())
                .build();

        thaoTacHoaDonService.add(thaoTacHoaDon);

        return repo.save(hoaDonRes);
    }
return null;
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
