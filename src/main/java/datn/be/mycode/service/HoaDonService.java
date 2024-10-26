package datn.be.mycode.service;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.VoucherHoaDonRequest;
import datn.be.mycode.request.customRequest.BanHang.*;
import datn.be.mycode.request.customRequest.TableHoaDonRequest;
import datn.be.mycode.response.HoaDonResponse;
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
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private HoaDonChiTietService hoaDonChiTietService;

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

        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<HoaDon> hoaDonResponsePage;
        hoaDonResponsePage = repo.searchAll(request.getKeyWord(), request.getStartDate(), request.getEndDate(), request.getIdKhachHang(), request.getIdNhanVien(), pageable);

        List<HoaDon> hoaDons = hoaDonResponsePage.getContent().stream().toList();
        List<HoaDonResponse> hoaDonResponses = new ArrayList<>();

        for (HoaDon hoaDon :hoaDons) {
            List<Voucher> vouchers = voucherHoaDonService.getVouchersByHoaDonId(hoaDon.getId());
            HoaDonResponse hoaDonResponse = new HoaDonResponse(hoaDon,vouchers);
            hoaDonResponses.add(hoaDonResponse);
        }

        var hoaDonNormalTableResponse = new NormalTableResponse<HoaDonResponse>(hoaDonResponses,request.getPage(),request.getPageSize(),hoaDonResponsePage.getTotalElements());


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

        var idVoucher = request.getIdVoucher();

        var voucher = voucherRepository.findById(request.getIdVoucher()).orElse(null);
        if(voucher == null || voucher.getTrangThai() != 1) {
            return getErro("không tìm thấy voucher hoặc voucher không hoạt động");
        }
        if(voucher.getSoLuong() <= 0){
            return getErro("Voucher này đã hết");
        }
        var voucherHoaDons = voucherHoaDonService.getVoucherHoaDonByHoaDon(hoaDon.getId());

//        check xem hoaDon có voucher này chưa
        boolean addNewVoucher = true;
        for (VoucherHoaDon voucherHoaDon:voucherHoaDons) {
            if(voucherHoaDon.getVoucher().getTheLoai() == voucher.getTheLoai()){
                voucherHoaDon.setVoucher(voucher);
                var updateVoucher = new VoucherHoaDonRequest(voucherHoaDon.getId(),voucherHoaDon.getNgayTao(),voucherHoaDon.getNgaySua(),voucherHoaDon.getTrangThai(),voucherHoaDon.getVoucher().getId(),voucherHoaDon.getHoaDon().getId());
                voucherHoaDonService.update(updateVoucher);
                addNewVoucher = false;
            }
        }
        if(addNewVoucher) {
            var addVoucher = new VoucherHoaDonRequest(voucher.getId(), hoaDon.getId());
            voucherHoaDonService.add(addVoucher);
        }

//
//                tính tiền ở đây
//
        return ResponseEntity.ok(hoaDon);
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

    public ResponseEntity<?> deleteVoucher(DeleteVoucher request) {

        HoaDon hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);
        if(hoaDon == null) return getErro("Hóa đơn không tồn tại");
//        kiểm tra hoa don da thanh toan hay chua
        if (hoaDon.getTrangThai() == 6) return getErro("Hóa đơn này không được thay đổi");
        voucherHoaDonService.delete(request.getIdHoaDon(),request.getIdVoucher());
        return ResponseEntity.ok(hoaDon);
    }

    public ResponseEntity<?> thanhToan(ThanhToanRequest request) {
        HoaDon hoaDon = repo.findById(request.getIdHoaDon()).orElse(null);
        if(hoaDon == null) return getErro("Hóa đơn này không tồn tại");
        if(checkVoucherForHoaDon(hoaDon)) return getErro("Có voucher đã hết hạn");

        String messCheckSoLuong = hoaDonChiTietService.checkSoLuong(hoaDon.getId());
        if(messCheckSoLuong != null) getErro(messCheckSoLuong);

        setHoaDon(hoaDon, request);
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setThanhToan(true);

        var phuongThucThanhToan = hoaDon.getPhuongThucThanhToan();
        if(phuongThucThanhToan.getId() == 2 ){
            hoaDon.setThanhToan(false);
            repo.save(hoaDon);

            return ResponseEntity.ok(taoThanhToan(hoaDon.getTongTien().intValue(), hoaDon.getMa()));
        }
        if(phuongThucThanhToan.getId() == 1 ){
            if(hoaDon.getGiaoHang()) {
                hoaDon.setThanhToan(false);
                repo.save(hoaDon);

                return ResponseEntity.ok(taoThanhToan(hoaDon.getTongTien().intValue(), hoaDon.getMa()));
            }

        }
        if(phuongThucThanhToan.getId() == 3 ){
            if(hoaDon.getGiaoHang()) {
                hoaDon.setThanhToan(false);
                repo.save(hoaDon);

            }
        }
        repo.save(hoaDon);
        return ResponseEntity.ok("Thành công");
    }

    private void setHoaDon(HoaDon hoaDon, ThanhToanRequest request) {
        hoaDon.setPhiShip(hoaDon.getGiaoHang() == true ? request.getPhiShip() : BigDecimal.valueOf(0));
        hoaDon.setTongTien(request.getTongTien());
        hoaDon.setTongTienSanPhamChuaGiam(request.getTongTienSanPhamChuaGiam());
        hoaDon.setTongTienGiam(request.getTongTienGiam());
        hoaDon.setTrangThai(request.getTrangThai());

    }

    private boolean checkVoucherForHoaDon(HoaDon hoaDon) {
        List<VoucherHoaDon> voucherHoaDonByHoaDons = voucherHoaDonService.getVoucherHoaDonByHoaDon(hoaDon.getId());
        LocalDateTime now = LocalDateTime.now();

        for (VoucherHoaDon voucherHoaDon:voucherHoaDonByHoaDons){

            LocalDateTime endDay = voucherHoaDon.getVoucher().getNgayKetThuc();
            if (endDay.isBefore(now)) {
                // Voucher đã quá hạn
                return false;
            }
            if (voucherHoaDon.getVoucher().getSoLuong()<=0) {
                // Voucher đã quá hạn
                return false;
            }
        }

        return false;
    }


    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

    public String taoThanhToan(int orderTotal,String maHoaDon) {
        return vnPayService.createOrder(orderTotal,maHoaDon);

    }
}
