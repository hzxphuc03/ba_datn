package datn.be.mycode.service;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.entity.ThaoTacHoaDon;
import datn.be.mycode.repository.HoaDonChiTietRepository;
import datn.be.mycode.repository.HoaDonRepository;
import datn.be.mycode.repository.SanPhamChiTietRepository;
import datn.be.mycode.request.HoaDonChiTietRequest;
import datn.be.mycode.request.HoaDonRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.customRequest.BanHang.AddSoLuong;
import datn.be.mycode.response.HoaDonChiTietResponse;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HoaDonChiTietService {

    @Autowired
    private HoaDonChiTietRepository repo;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private GiamGiaService giamGiaService;
    @Autowired
    private SanPhamService sanPhamService;

    public NormalTableResponse<HoaDonChiTiet> getAllByIdHoaDon(NormalTableRequest request) {

        CheckRequest(request);


        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<HoaDonChiTiet> hoaDonResponsePage;
        List<HoaDonChiTiet> hoaDonChiTiets;
        var hoaDonNormalTableResponse = new NormalTableResponse<HoaDonChiTiet>();

        hoaDonResponsePage = repo.findAllByHoaDon_Id(Long.valueOf(request.getKeyWord()), pageable);
        hoaDonChiTiets = hoaDonResponsePage.getContent();

        CheckGiamGia(hoaDonChiTiets);

        hoaDonNormalTableResponse.setItem(hoaDonChiTiets);
        hoaDonNormalTableResponse.setPage(request.getPage());
        hoaDonNormalTableResponse.setPageSize(request.getPageSize());
        hoaDonNormalTableResponse.setTotalItem(hoaDonResponsePage.getTotalElements());

        return hoaDonNormalTableResponse;
    }

    private void CheckGiamGia(List<HoaDonChiTiet> hoaDonChiTiets) {
        for (HoaDonChiTiet hoaDonChiTiet :hoaDonChiTiets) {
            var sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
            var sanPham = sanPhamChiTiet.getSanPham();
            var giamGia = sanPham.getGiamGia();

            if(giamGia!=null && giamGia.getNgayKetThuc().isBefore(LocalDateTime.now())){
                giamGia.setTrangThai(3);
                sanPham.setGiamGia(null);

                hoaDonChiTiet.setGiaDaGiam(sanPhamChiTiet.getGiaBan());

                giamGiaService.update(giamGia);
                sanPhamService.update(sanPham);
                repo.save(hoaDonChiTiet);
            }

        }
    }

    private void CheckRequest(NormalTableRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        if (request.getStatus() == null) {
            request.setStatus(1);
        }
    }

    public ResponseEntity<?> add(HoaDonChiTietRequest request){

        HoaDonChiTiet hoaDonChiTiet = repo.findByHoaDonIdAndSanPhamChiTietId(request.getIdHoaDon(), request.getIdSanPhamChiTiet());
        var hd = hoaDonRepository.findById(request.getIdHoaDon()).orElse(null);
        var spct = sanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null);
        var giamGia = spct.getSanPham().getGiamGia();
        var gia = spct.getGiaBan();

        if(request.getSoLuong() <= 0) return getErro("Số lượng phải lớn hơn 0");
        if(request.getSoLuong()>spct.getSoLuong()) return getErro("Số lượng sản phẩm không vượt quá số lượng đang có");


        if(giamGia != null && giamGia.getNgayBatDau().isBefore(LocalDateTime.now()) && giamGia.getNgayKetThuc().isAfter(LocalDateTime.now())){
            var mucGiam = BigDecimal.valueOf(giamGia.getMucGiam()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            gia = gia.subtract(gia.multiply(mucGiam));
        }else {
            gia = spct.getGiaBan();
        }

        if(hoaDonChiTiet==null) {
            if(hd!=null && spct!=null) {
                hoaDonChiTiet = new HoaDonChiTiet().builder()
                        .soLuong(request.getSoLuong())
                        .giaHienHanh(gia)
                        .giaDaGiam(gia)
                        .trangThai(1)
                        .hoaDon(hd)
                        .sanPhamChiTiet(spct)
                        .build();
            }
            else{
                return getErro("Hóa đơn hoặc sản phẩm này chưa tồn tại");
            }
        }

        else{
            hoaDonChiTiet.AddSoluong(request.getSoLuong());
            if(hoaDonChiTiet.getSoLuong()>spct.getSoLuong()) return getErro("Số lượng sản phẩm trong hóa đơn không vượt quá số lượng sản phẩm đang có");
        }

        var hoaDonChiTietRes = new HoaDonChiTietResponse(repo.save(hoaDonChiTiet));

        return ResponseEntity.ok(hoaDonChiTietRes);
    }


    public ResponseEntity<?> delete(Long id){
        try{
            var hoaDonChiTiet = repo.findById(id).get();
            repo.delete(hoaDonChiTiet);
            return ResponseEntity.ok("Xóa thành công");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(400,"Xóa thất bại"));
        }
    }

    public ResponseEntity<?> addSoLuong(AddSoLuong request) {
        var hoaDonChiTiet = repo.findById(request.getIdHoaDonChiTiet()).orElse(null);
        if(hoaDonChiTiet != null){
            var spct = hoaDonChiTiet.getSanPhamChiTiet();
            if(request.getSoLuong() <= 0) return getErro("Số lượng phải lớn hơn 0");
            int soLuongSauThem = request.getSoLuong()+hoaDonChiTiet.getSoLuong();
            if(soLuongSauThem>spct.getSoLuong()) return getErro("Số lượng vượt quá số lượng đang có");
            hoaDonChiTiet.setSoLuong(soLuongSauThem);
            return ResponseEntity.ok(repo.save(hoaDonChiTiet));
        }
        return getErro("Không tìm thấy hóa đơn chi tiết");
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

}
