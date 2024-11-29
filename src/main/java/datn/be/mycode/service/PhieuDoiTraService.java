package datn.be.mycode.service;

import datn.be.mycode.entity.*;
import datn.be.mycode.repository.*;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.AddPhieuDoiTraReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.AddSanPhamDoiTraReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.GetPhieuDoiTraReq;
import datn.be.mycode.request.DoiTra.PhieuDoiTra.XacNhanReq;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhieuDoiTraService {

    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private PhieuDoiTraRepository phieuDoiTraRepository;
    @Autowired
    private SanPhamDoiTraRepository sanPhamDoiTraRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private ThongTinKiemTraRepositpry thongTinKiemTraRepositpry;
    @Autowired
    private ThongTinLyDoDoiTraRepository thongTinLyDoDoiTraRepository;

    public Object getPhieuDoiTra(GetPhieuDoiTraReq request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        if (request.getKeyWord() == null || request.getKeyWord().isBlank()){
            request.setKeyWord(null);
        }
        if (request.getMaPhieuDoiTra() == null || request.getMaPhieuDoiTra().isBlank()){
            request.setKeyWord(null);
        }
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        var phieuGiamGiaPage = phieuDoiTraRepository.findAllByKhachHangAndStatus(request.getKeyWord(), request.getMaPhieuDoiTra(), request.getStatus(), pageable);


        var phieuDoiTraNormalTableResponse = new NormalTableResponse<PhieuDoiTra>();
        phieuDoiTraNormalTableResponse.setItem(phieuGiamGiaPage.getContent());
        phieuDoiTraNormalTableResponse.setPage(request.getPage());
        phieuDoiTraNormalTableResponse.setPageSize(request.getPageSize());
        phieuDoiTraNormalTableResponse.setTotalItem(phieuGiamGiaPage.getTotalElements());
        return phieuDoiTraNormalTableResponse;
    }

    public Object addPhieuDoiTra(AddPhieuDoiTraReq request) {

        String erroMess = validateAddPhieuDoiTra(request);
        if(!erroMess.isBlank()) return getErro(erroMess);
        HoaDon hoaDonCu = hoaDonRepository.findById(request.getIdHoaDon()).orElse(null);
        if(hoaDonCu == null) return getErro("Hóa đơn này không tồn tại");

//        San Pham Doi Tra
        List<SanPhamDoiTra> sanPhamDoiTraList = new ArrayList<>();
        for (AddSanPhamDoiTraReq addSanPhamDoiTraReq : request.getSanPhamDoiTraList()) {
            var hoaDonChiTiet = hoaDonChiTietRepository.findById(addSanPhamDoiTraReq.getIdHoaDonChiTiet()).orElse(null);
            if(hoaDonChiTiet == null) return getErro("Hóa đơn chi tiết này không tồn tại");
            var soLuongDoiCuaSanPham = addSanPhamDoiTraReq.getSoLuong();
            if (soLuongDoiCuaSanPham>hoaDonChiTiet.getSoLuong()) return getErro("Không vượt quá số lượng đã mua");
            if (soLuongDoiCuaSanPham <=0) return getErro("số Lượng phải lớn hơn 0");
            if (soLuongDoiCuaSanPham == null) return getErro("Bạn phải nhập số lượng");
            if (!hoaDonCu.equals(hoaDonChiTiet.getHoaDon())) return getErro("hóa đơn không có sản phẩm này");
            SanPhamDoiTra sanPhamDoiTra = new SanPhamDoiTra();
            sanPhamDoiTra.setSoLuong(soLuongDoiCuaSanPham);
            sanPhamDoiTra.setGia_san_pham(hoaDonChiTiet.getGiaDaGiam());
            sanPhamDoiTra.setTrangThai(1);
            sanPhamDoiTra.setSanPham(hoaDonChiTiet.getSanPhamChiTiet());

            sanPhamDoiTra = sanPhamDoiTraRepository.save(sanPhamDoiTra);
            sanPhamDoiTraList.add(sanPhamDoiTra);
        }


        var khachHang = hoaDonCu.getKhachHang();

        PhieuDoiTra phieuDoiTra = new PhieuDoiTra();
//        phieuDoiTra.setSoTaiKhoanNganHang(request.getSoTaiKhoanNganHang());
//        phieuDoiTra.setTenNganHang(request.getTenNganHang());
//        phieuDoiTra.setTenChuTaiKhoan(request.getTenChuTaiKhoan());
        phieuDoiTra.setNgayTao(LocalDateTime.now());
        phieuDoiTra.setLyDoDoiTra(request.getLyDoDoiTra());
        phieuDoiTra.setThaoTac(1);
        phieuDoiTra.setTheLoai(request.getTheLoai());
        phieuDoiTra.setTrangThai(1);
        phieuDoiTra.setHoaDonCu(hoaDonCu);
        phieuDoiTra.setKhachHang(khachHang);

        var phieuDoiTraGet = phieuDoiTraRepository.save(phieuDoiTra);

//        Thong tin ly do doi tra
        request.getHinhAnhList().forEach(s -> {

        ThongTinLyDoDoiTra thongTinLyDoDoiTra = new ThongTinLyDoDoiTra();
        thongTinLyDoDoiTra.setVideo(s);
        thongTinLyDoDoiTra.setTrangThai(1);
        thongTinLyDoDoiTra.setPhieuDoiTra(phieuDoiTraGet);
        thongTinLyDoDoiTraRepository.save(thongTinLyDoDoiTra);
        });

        sanPhamDoiTraList.forEach(sanPhamDoiTra -> {
            sanPhamDoiTra.setPhieuDoiTra(phieuDoiTraGet);
            sanPhamDoiTraRepository.save(sanPhamDoiTra);
        });

        phieuDoiTraGet.setMa("PDT"+phieuDoiTraGet.getId());
        return ResponseEntity.ok(phieuDoiTraRepository.save(phieuDoiTraGet));
    }

    private String validateAddPhieuDoiTra(AddPhieuDoiTraReq request) {
//        if(request.getSoTaiKhoanNganHang().isBlank()) return "Bạn chưa nhập số tài khoản ngân hàng";
//        if(request.getTenChuTaiKhoan().isBlank()) return "Bạn chưa nhập tên chủ tài khoản ngân hàng";
        if(request.getHinhAnhList().isEmpty()) return "Bạn chưa thêm video hoặc ảnh";
        if(request.getLyDoDoiTra().isBlank()) return "Bạn chưa nhập lý do đổi hàng";
        if(request.getTheLoai() == null) return "Bạn chưa chọn thể loại";
        return "";
    }

    public Object xacNhanPhieuDoiTra(XacNhanReq request, int thaoTac) {
        var nhanVien = nhanVienRepository.findById(request.getIdNhanVien()).orElse(null);
        if (nhanVien == null) return getErro("Nhân viên này chưa có tài khoản hoặc đã nghỉ");
        var phieuDoiTra = phieuDoiTraRepository.findById(request.getIdPhieuDoiTra()).orElse(null);
        if (phieuDoiTra == null) return getErro("Không tìm thấy phiếu đổi trả này");
        if(phieuDoiTra.getTrangThai() == 0) return getErro("Không được thao tác trên phiếu đổi trả này");

        phieuDoiTra.setNhanVien(nhanVien);
        phieuDoiTra.setThaoTac(thaoTac);
        phieuDoiTra.setTrangThai(2);
        if (thaoTac == 0) phieuDoiTra.setTrangThai(0);

        return ResponseEntity.ok(phieuDoiTraRepository.save(phieuDoiTra));
    }



    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
