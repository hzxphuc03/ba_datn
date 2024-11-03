package datn.be.mycode.service;

import datn.be.mycode.entity.SuKien;
import datn.be.mycode.repository.SuKienRepository;
import datn.be.mycode.request.SuKienRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.erroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SuKienService {
    @Autowired
    SuKienRepository suKienRepository;

    public NormalTableResponse<SuKien> getAll(TableLongRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        NormalTableResponse<SuKien> suKienNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<SuKien> suKienPage;

        if (request.getKeyWord() != null) {
            suKienPage = suKienRepository.findAllById(request.getKeyWord(), pageable);
        } else if (request.getStatus() != null){
            suKienPage = suKienRepository.findAllByTrangThai(request.getStatus(), pageable);
        }else {
            suKienPage = suKienRepository.findAll( pageable);
        }

        List<SuKien> suKiens = suKienPage.getContent().stream().toList();

        suKienNormalTableResponse.setItem(suKiens);
        suKienNormalTableResponse.setPage(request.getPage());
        suKienNormalTableResponse.setPageSize(request.getPageSize());
        suKienNormalTableResponse.setTotalItem(suKienPage.getTotalElements());

        return suKienNormalTableResponse;
    }


    public ResponseEntity<?> add(SuKienRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra ngày bắt đầu sự kiện phải sau hôm nay
        if (request.getNgayBatDau().isBefore(now.plusDays(1))) {
            return getErro("Ngày bắt đầu sự kiện phải sau ngày hiện tại ít nhất 1 ngày.");
        }
//        System.out.println(request);
        SuKien suKien = new SuKien();
        suKien.setTenSuKien(request.getTenSuKien());
        suKien.setMaSuKien(request.getMaSuKien());
        suKien.setNgayBatDau(request.getNgayBatDau());
        suKien.setNgayKetThuc(request.getNgayKetThuc());
        suKien.setKeySuKien(request.getKeySuKien());
        suKien.setTrangThai(2);
        SuKien savedSuKien = suKienRepository.save(suKien);
        capNhatTrangThaiSuKien();
        return ResponseEntity.ok(savedSuKien);
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

    public ResponseEntity<?> update(SuKienRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra nếu sự kiện không tồn tại
        Optional<SuKien> suKienOptional = suKienRepository.findById(request.getId());
        if (suKienOptional.isEmpty()) {
            return getErro("Sự kiện không tồn tại.");
        }

        SuKien suKien = suKienOptional.get();

        // Kiểm tra ngày bắt đầu phải sau ngày hiện tại
        if (request.getNgayBatDau().isBefore(now.plusDays(1))) {
            return getErro("Ngày bắt đầu sự kiện phải sau ngày hiện tại ít nhất 1 ngày.");
        }

        // Cập nhật các thông tin của sự kiện
        suKien.setTenSuKien(request.getTenSuKien());
        suKien.setMaSuKien(request.getMaSuKien());
        suKien.setNgayBatDau(request.getNgayBatDau());
        suKien.setNgayKetThuc(request.getNgayKetThuc());
        suKien.setKeySuKien(request.getKeySuKien());
        suKien.setTrangThai(request.getTrangThai());

        // Lưu lại sự kiện sau khi cập nhật
        SuKien updatedSuKien = suKienRepository.save(suKien);
        capNhatTrangThaiSuKien();
        // Trả về phản hồi thành công với sự kiện đã cập nhật
        return ResponseEntity.ok(updatedSuKien);
    }

    @Transactional
    public void capNhatTrangThaiSuKien() {
        LocalDateTime now = LocalDateTime.now();
        // Lấy tất cả Sự kiện
        List<SuKien> suKiens = suKienRepository.findAll();
        for (SuKien suKien : suKiens) {
            LocalDateTime startDay = suKien.getNgayBatDau();
            LocalDateTime endDay = suKien.getNgayKetThuc();
            if (endDay.isBefore(now)) {
                // Sự kiện đã qua
                suKien.setTrangThai(0);
            } else if (startDay.isAfter(now)) {
                // Sự kiện chưa đến
                suKien.setTrangThai(2);
            } else if (startDay.isBefore(now) || startDay.isEqual(now)) {
                // Sự kiện đang trong thời gian sự kiện (startDay <= now <= endDay)
                suKien.setTrangThai(1);
            }
            // Lưu lại sự kiện nếu trạng thái thay đổi
            suKienRepository.save(suKien);
        }

    }

    public SuKien udateTrangThai(Long id, Integer trangThai){
        SuKien suKien = suKienRepository.findById(id).get();
        suKien.setTrangThai(trangThai);
        return suKienRepository.save(suKien);
    }

}
