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

import java.util.List;

@Service
public class HoaDonChiTietService {

    @Autowired
    private HoaDonChiTietRepository repo;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public NormalTableResponse<HoaDonChiTiet> getAllByIdHoaDon(NormalTableRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<HoaDonChiTiet> hoaDonNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<HoaDonChiTiet> hoaDonResponsePage;
        hoaDonResponsePage = repo.findAllByHoaDon_Id(Long.valueOf(request.getKeyWord()), pageable);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonResponsePage.getContent();

        hoaDonNormalTableResponse.setItem(hoaDonChiTiets);
        hoaDonNormalTableResponse.setPage(request.getPage());
        hoaDonNormalTableResponse.setPageSize(request.getPageSize());
        hoaDonNormalTableResponse.setTotalItem(hoaDonResponsePage.getTotalElements());

        return hoaDonNormalTableResponse;
    }

    public ResponseEntity<?> add(HoaDonChiTietRequest request){

        HoaDonChiTiet hoaDonChiTiet = repo.findByHoaDonIdAndSanPhamChiTietId(request.getIdHoaDon(), request.getIdSanPhamChiTiet());

        if(request.getSoLuong() == 0) repo.delete(hoaDonChiTiet);


        if(hoaDonChiTiet==null) {
            var hd = hoaDonRepository.findById(request.getIdHoaDon()).orElse(null);
            var spct = sanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null);
            if(hd!=null && spct!=null) {
                hoaDonChiTiet = new HoaDonChiTiet().builder()
                        .soLuong(request.getSoLuong())
                        .giaHienHanh(request.getGiaHienHanh())
                        .giaDaGiam(request.getGiaDaGiam())
                        .trangThai(1)
                        .hoaDon(hd)
                        .sanPhamChiTiet(spct)
                        .build();
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(400,"Hóa đơn hoặc sản phẩm này chưa tồn tại"));
            }
        }
        else{
            hoaDonChiTiet.AddSoluong(request.getSoLuong());
        }


        return ResponseEntity.ok(repo.save(hoaDonChiTiet));
    }
}
