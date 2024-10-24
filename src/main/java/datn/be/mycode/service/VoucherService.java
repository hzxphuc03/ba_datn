package datn.be.mycode.service;


import datn.be.mycode.entity.Voucher;
import datn.be.mycode.repository.VoucherRepository;
import datn.be.mycode.request.VoucherRequest;
import datn.be.mycode.request.customRequest.TableVoucherRequest;
import datn.be.mycode.request.customRequest.TableVoucherStatusInRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherService {
    @Autowired
    VoucherRepository repository;


    public NormalTableResponse<Voucher> getAll(TableVoucherRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }


        NormalTableResponse<Voucher> voucherNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<Voucher> voucherPage;
        if (request.getKeyWord() != null ) {
            voucherPage = repository.searchByTenOrMa(request.getKeyWord(), pageable);
        }else if (request.getStartDate() != null & request.getStartDate() != null){
            voucherPage = repository.searchByNgayTao(request.getStartDate(),request.getEndDate(), pageable);
        }else if (request.getPhanTramGiam() != null ){
            voucherPage = repository.searchByPhanTramGiam(request.getPhanTramGiam(), pageable);
        } else if (request.getStatus() != null){
            voucherPage = repository.findAllByTrangThai(request.getStatus(), pageable);
        }else{
            voucherPage = repository.findAll(pageable);
        }

        List<Voucher> vouchers = voucherPage.getContent().stream().toList();

        voucherNormalTableResponse.setItem(vouchers);
        voucherNormalTableResponse.setPage(request.getPage());
        voucherNormalTableResponse.setPageSize(request.getPageSize());
        voucherNormalTableResponse.setTotalItem(voucherPage.getTotalElements());

        return voucherNormalTableResponse;
    }



    public Voucher add(VoucherRequest request) {
        System.out.println("addDC1");
        System.out.println(request);
        Voucher voucher = new Voucher();
        voucher.setMa(request.getMa());
        voucher.setTen(request.getTen());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        voucher.setNgayTao(LocalDateTime.now());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setPhanTramGiam(request.getPhanTramGiam());
        voucher.setGiamToiDa(request.getGiamToiDa());
        voucher.setGiaTriGiamToiThieu(request.getGiaTriGiamToiThieu());
        voucher.setTrangThai(request.getTrangThai());

        return repository.save(voucher);
    }

    public Voucher update(VoucherRequest request) {
        Voucher voucher = repository.findById(request.getId()).get();
        voucher.setMa(request.getMa());
        voucher.setTen(request.getTen());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        voucher.setNgaySua(LocalDateTime.now());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setPhanTramGiam(request.getPhanTramGiam());
        voucher.setGiamToiDa(request.getGiamToiDa());
        voucher.setGiaTriGiamToiThieu(request.getGiaTriGiamToiThieu());
        voucher.setTrangThai(request.getTrangThai());
        return repository.save(voucher);
    }

    public Voucher udateTrangThai(Long id, Integer status){
        Voucher voucher = repository.findById(id).get();
        voucher.setTrangThai(status);
        return repository.save(voucher);
    }

    public Voucher getVoucherById(Long idVoucher) {
        return repository.searchById(idVoucher).get();
    }

    /**
     * Cập nhật trạng thái của các voucher dựa trên ngày hiện tại.
     * Trạng thái = 3 nếu voucher đã quá hạn (endDay < ngày hiện tại).
     * Trạng thái = 0 nếu chưa đến sự kiện (startDay > ngày hiện tại).
     * Trạng thái = 1 nếu đang trong thời gian sự kiện (startDay <= ngày hiện tại <= endDay).
     */
    @Transactional
    public void capNhatTrangThaiVoucher() {
        LocalDateTime now = LocalDateTime.now();
        // Lấy tất cả voucher
        List<Voucher> vouchers = repository.findAll();
        for (Voucher voucher : vouchers) {
            LocalDateTime startDay = voucher.getNgayBatDau();
            LocalDateTime endDay = voucher.getNgayKetThuc();

            if (endDay.isBefore(now)) {
                // Voucher đã quá hạn
                voucher.setTrangThai(3);
            } else if (startDay.isAfter(now)) {
                // Voucher chưa đến sự kiện
                voucher.setTrangThai(0);
            } else if (startDay.isBefore(now) || startDay.isEqual(now)) {
                // Voucher đang trong thời gian sự kiện (startDay <= now <= endDay)
                voucher.setTrangThai(1);
            }
            // Lưu lại voucher nếu trạng thái thay đổi
            repository.save(voucher);
        }
    }

    public NormalTableResponse<Voucher> trongSuKien(TableVoucherStatusInRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }


        LocalDateTime today = LocalDateTime.now();
        NormalTableResponse<Voucher> voucherNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<Voucher> voucherPage;
            voucherPage = repository.findByNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(today,today, pageable);

        List<Voucher> vouchers = voucherPage.getContent().stream().toList();

        voucherNormalTableResponse.setItem(vouchers);
        voucherNormalTableResponse.setPage(request.getPage());
        voucherNormalTableResponse.setPageSize(request.getPageSize());
        voucherNormalTableResponse.setTotalItem(voucherPage.getTotalElements());

        return voucherNormalTableResponse;
    }

}
