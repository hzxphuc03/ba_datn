package datn.be.mycode.service;

import datn.be.mycode.entity.ThaoTacHoaDon;
import datn.be.mycode.repository.HoaDonRepository;
import datn.be.mycode.repository.NhanVienRepository;
import datn.be.mycode.repository.ThaoTacHoaDonRepository;
import datn.be.mycode.request.ThaoTacHoaDonRequest;
import datn.be.mycode.request.customRequest.TableThaoTacHoaDonRequest;
import datn.be.mycode.response.ThaoTacHoaDonResponse;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThaoTacHoaDonService {

    @Autowired
    private ThaoTacHoaDonRepository thaoTacHoaDonRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;

    public NormalTableResponse<ThaoTacHoaDonResponse> getAll(TableThaoTacHoaDonRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }
        NormalTableResponse<ThaoTacHoaDonResponse> hoaDonNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<ThaoTacHoaDonResponse> hoaDonResponsePage;
        hoaDonResponsePage = thaoTacHoaDonRepository.searchAll( request.getIdHoaDon(), request.getIdNhanVien(), pageable);
        List<ThaoTacHoaDonResponse> hoaDonResponses = hoaDonResponsePage.getContent().stream().toList();

        hoaDonNormalTableResponse.setItem(hoaDonResponses);
        hoaDonNormalTableResponse.setPage(request.getPage());
        hoaDonNormalTableResponse.setPageSize(request.getPageSize());
        hoaDonNormalTableResponse.setTotalItem(hoaDonResponsePage.getTotalElements());

        return hoaDonNormalTableResponse;
    }

    public ThaoTacHoaDon add(ThaoTacHoaDon request){

        ThaoTacHoaDon thaoTacHoaDon = new ThaoTacHoaDon().builder()
                .thaoTac(request.getThaoTac())
                .ngayTao(LocalDateTime.now())
                .trangThai(request.getTrangThai())
                .hoaDon(hoaDonRepository.findById(request.getHoaDon().getId()).get())
                .nhanVien(nhanVienRepository.findById(request.getNhanVien().getId()).get())
                .build();
        return thaoTacHoaDonRepository.save(thaoTacHoaDon);
    }
}
