package datn.be.mycode.service;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.VoucherHoaDonRequest;
import datn.be.mycode.request.customRequest.BanHang.AddObject;
import datn.be.mycode.request.customRequest.BanHang.AddDiaChi;
import datn.be.mycode.request.customRequest.BanHang.AddVoucher;
import datn.be.mycode.request.customRequest.BanHang.updateGiaoHang;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherHoaDonService voucherHoaDonService;

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
                .phiShip(request.getPhiShip())
                .tongTien(request.getTongTien())
                .tongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam())
                .tongTienGiam(request.getTongTienGiam())
                .giaoHang(false)
                .thanhToan(false)
                .phuongThucThanhToan(phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).get())
                .nhanVien(nhanVienRepository.findById(request.getIdNhanVien()).get())
                .khachHang(khachHangRepository.findById(request.getIdKhachHang()).get())
                .build();

        HoaDon hoaDonRes = repo.save(hoaDon);
        hoaDonRes.setMa("HD"+hoaDonRes.getId());
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
                .giaoHang(false)
                .thanhToan(false)
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

    public ResponseEntity<?> hoaDonAddDiaChi(AddDiaChi request) {
        var hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);

        if (hoaDon != null){

            if(checkDiaChi(request)){
                getDiaChi(hoaDon, request);
                hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
                hoaDon.setSdtNguoiNhan(request.getSdtNguoiNhan());
                return ResponseEntity.ok(repo.save(hoaDon));
            }
            return getErro("Địa chỉ hoặc thông tin người nhận trống");
        }

        return getErro("Hóa đơn không tồn tại");
    }

    private boolean checkDiaChi(AddDiaChi request) {
        boolean check = false;
        if (!request.getGhiChu().trim().isEmpty()){
            check =true;
        }
        if (!request.getTenNguoiNhan().trim().isEmpty()){
            check =true;
        }
        if (!request.getSdtNguoiNhan().trim().isEmpty()){
            check =true;
        }
        return check;
    }

    private void getDiaChi(HoaDon hoaDon, AddDiaChi request) {
        hoaDon.setIdTinhThanh(request.getIdTinhThanh());
        hoaDon.setTinhThanh(request.getTinhThanh());
        hoaDon.setIdQuanHuyen(request.getIdQuanHuyen());
        hoaDon.setQuanHuyen(request.getQuanHuyen());
        hoaDon.setIdPhuongXa(request.getIdPhuongXa());
        hoaDon.setPhuongXa(request.getPhuongXa());
        hoaDon.setGhiChu(request.getGhiChu());
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

    public ResponseEntity<?> hoaDonAddKhachHang(AddObject request) {
        var hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);
        var khachHang = khachHangRepository.findById(request.getIdObject()).orElse(null);

        if (hoaDon != null){
            if(khachHang != null) {
                hoaDon.setKhachHang(khachHang);
                return ResponseEntity.ok(repo.save(hoaDon));
            }
            return getErro("Khách hàng không tồn tại");
        }
        return getErro("Hóa đơn không tồn tại");
    }

    public ResponseEntity<?> hoaDonAddVoucher(AddVoucher request) {

        var hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);
        if (hoaDon == null) return getErro("Hóa đơn không tồn tại");

        var idVouchers = request.getIdVouchers();
        if(idVouchers.size() > 2) return getErro("Hóa đơn chỉ có 2 voucher");

        var vouchers = getListVoucher(idVouchers);
        if(vouchers.get(0).getTrangThai() == vouchers.get(1).getTrangThai()) {
            return getErro("Voucher cùng 1 loại");
        }

        var voucherHoaDons = voucherHoaDonService.getVoucherHoaDonByHoaDon(hoaDon.getId());


        if (voucherHoaDons.size() == 2){
            for (int i = 0; i < 2; i++) {
                var voucherHoaDon = voucherHoaDons.get(i);
                voucherHoaDon.setVoucher(vouchers.get(i));
                var updateVoucher = new VoucherHoaDonRequest(voucherHoaDon.getId(),voucherHoaDon.getNgayTao(),voucherHoaDon.getNgaySua(),voucherHoaDon.getTrangThai(),voucherHoaDon.getVoucher().getId(),voucherHoaDon.getHoaDon().getId());
                voucherHoaDonService.update(updateVoucher);
            }
            return ResponseEntity.ok(hoaDon);
        }

        for (Voucher voucher:vouchers) {
            var addVoucher = new VoucherHoaDonRequest(voucher.getId(),hoaDon.getId());
            voucherHoaDonService.add(addVoucher);

        }

//
//                tính tiền ở đây
//
        return ResponseEntity.ok(hoaDon);
    }

    private List<Voucher> getListVoucher(List<Long> idVouchers) {
        var vouchers = new ArrayList<Voucher>();
        for (int i = 0; i < 2; i++) {
            var voucher = voucherRepository.findById(idVouchers.get(i)).orElse(null);
            if(voucher != null){
                vouchers.add(voucher);
            }
        }
        return vouchers;
    }

    public ResponseEntity<?> hoaDonAddPhuongThucThanhToan(AddObject request) {
        var hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);
        var phuongThucThanhToan = phuongThucThanhToanRepository.findById(request.getIdObject()).orElse(null);

        if (hoaDon != null){
            if(phuongThucThanhToan != null) {
                hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);
                return ResponseEntity.ok(repo.save(hoaDon));
            }
            return getErro("Phương thức thanh toán không tồn tại");
        }
        return getErro("Hóa đơn không tồn tại");
    }

    public ResponseEntity<?> isGiaoHangForHoaDon(updateGiaoHang request) {
        var hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);

        if (hoaDon == null){
            return getErro("Hóa đơn không tồn tại");
        }

        hoaDon.setGiaoHang(request.getGiaohang());

        return ResponseEntity.ok(repo.save(hoaDon));
    }
}
