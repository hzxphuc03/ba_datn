package datn.be.mycode.service;

import datn.be.mycode.entity.SuKien;
import datn.be.mycode.repository.SuKienRepository;
import datn.be.mycode.request.SuKienRequest;
import datn.be.mycode.request.customRequest.TableLongRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.response.SuKien.ThangActive;
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
import java.util.*;
import java.util.stream.Collectors;

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
        var donViPhieuGiamGia = request.getDonViPhieuGiamGia();
        if(donViPhieuGiamGia > 2000) return getErro("Đợn vị phiếu giảm giá không quá 2000");
        if(donViPhieuGiamGia <= 0 ) return getErro("Đợn vị phiếu giảm giá phải lơn hơn 0");
        var thoiGianHetHanPhieu = request.getThoiGianHetHanPhieu();
        if(thoiGianHetHanPhieu > 30) return getErro("Thời gian hết hạn phiếu giảm giá không quá 30");
        if(thoiGianHetHanPhieu <= 0) return getErro("Thời gian hết hạn phiếu giảm giá phải lơn hơn 0");

        if(request.getTenSuKien().isBlank()) return getErro("Không để trống tên");
        if(request.getDonViPhieuGiamGia() == null) return getErro("Không để trống số lượng mỗi loại phiếu giảm giá");
        if(request.getThoiGianHetHanPhieu() == null) return getErro("Không để trống thời gian hết hạn phiếu");

        var thangHoatDong = request.getThangHoatDong().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        if(thangHoatDong.isBlank())return getErro("Cần chọn tháng cho sự kiện");

        // Kiểm tra tháng bắt đầu sự kiện, tháng cuối phải sau tháng hiện tại

        SuKien suKien = new SuKien();
        suKien.setTenSuKien(request.getTenSuKien());
        suKien.setThangHoatDong(thangHoatDong);
        suKien.setDonViPhieuGiamGia(donViPhieuGiamGia);// <= 2000
        suKien.setThoiGianHetHanPhieu(thoiGianHetHanPhieu);// <= 30
        suKien.setTrangThai(0);

        SuKien savedSuKien = suKienRepository.save(suKien);
        savedSuKien.setMaSuKien("SK"+savedSuKien.getId());
        savedSuKien.setKeySuKien(savedSuKien.getMaSuKien()+"-"+now.getYear());

        return ResponseEntity.ok(suKienRepository.save(savedSuKien));
    }

    private ResponseEntity<?> getErro(String s){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(s));
    }

//    public ResponseEntity<?> update(SuKienRequest request) {
//        LocalDateTime now = LocalDateTime.now();
//
//        // Kiểm tra nếu sự kiện không tồn tại
//        Optional<SuKien> suKienOptional = suKienRepository.findById(request.getId());
//        if (suKienOptional.isEmpty()) {
//            return getErro("Sự kiện không tồn tại.");
//        }
//
//        SuKien suKien = suKienOptional.get();
//
//        // Kiểm tra ngày bắt đầu phải sau ngày hiện tại
//        if (request.getNgayBatDau().isBefore(now.plusDays(1))) {
//            return getErro("Ngày bắt đầu sự kiện phải sau ngày hiện tại ít nhất 1 ngày.");
//        }
//
//        // Cập nhật các thông tin của sự kiện
//        suKien.setTenSuKien(request.getTenSuKien());
//        suKien.setMaSuKien(request.getMaSuKien());
//        suKien.setNgayBatDau(request.getNgayBatDau());
//        suKien.setNgayKetThuc(request.getNgayKetThuc());
//        suKien.setKeySuKien(request.getKeySuKien());
//        suKien.setTrangThai(request.getTrangThai());
//
//        // Lưu lại sự kiện sau khi cập nhật
//        SuKien updatedSuKien = suKienRepository.save(suKien);
//        capNhatTrangThaiSuKien();
//        // Trả về phản hồi thành công với sự kiện đã cập nhật
//        return ResponseEntity.ok(updatedSuKien);
//    }


    public SuKien udateTrangThai(Long id, Integer trangThai){
        SuKien suKien = suKienRepository.findById(id).get();
        suKien.setTrangThai(trangThai);
        return suKienRepository.save(suKien);
    }

    public Object getThangActive() {
        List<ThangActive> thangActives = new ArrayList<>();

        var sukiens = suKienRepository.findAllWithActiveMonths();
        Set<Integer> activeMonths = new HashSet<>();
        for (SuKien suKien:sukiens) {
            String[] monthsArray = suKien.getThangHoatDong().split(",");
            for (String month : monthsArray) {
                activeMonths.add(Integer.parseInt(month.trim()));
            }
        }
        for (int i = 1; i <= 12; i++) {
            thangActives.add(new ThangActive(i, activeMonths.contains(i))); // Kiểm tra xem tháng có trong Set không
        }

        return thangActives;
    }

    public ResponseEntity<?> getDetailSuKien(Long idSuKien) {
        var suKien = suKienRepository.findById(idSuKien).orElse(null);
        if(suKien == null) return getErro("Sự kiện này không tồn tại");

        return ResponseEntity.ok(suKien);
    }

    public Object loadSuKien(){

        var now = LocalDateTime.now();
        var suKiens = suKienRepository.findAllWithActiveMonths();
        for (SuKien suKien:suKiens) {
            List<Integer> activeMonths = new ArrayList<>();
            String[] monthsArray = suKien.getThangHoatDong().split(",");
            for (String month : monthsArray) {
                activeMonths.add(Integer.parseInt(month.trim()));
            }
            if(activeMonths.get(0)>now.getMonth().getValue()){
                suKien.setTrangThai(0);
            }
            if(activeMonths.get(activeMonths.size()-1)<now.getMonth().getValue()){
                suKien.setTrangThai(0);
            }
            if(activeMonths.get(activeMonths.size()-1)>=now.getMonth().getValue() && activeMonths.get(0)<=now.getMonth().getValue()){
                suKien.setTrangThai(1);
            }
            suKienRepository.save(suKien);
        }

        return "ok";

    }
}
