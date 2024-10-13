package datn.be.mycode.service;

import datn.be.mycode.entity.VoucherHoaDon;
import datn.be.mycode.repository.HoaDonRepository;
import datn.be.mycode.repository.VoucherHoaDonRepository;
import datn.be.mycode.repository.VoucherRepository;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.VoucherHoaDonRequest;
import datn.be.mycode.response.NormalTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherHoaDonService {
    @Autowired
    VoucherHoaDonRepository vhRepo;
    @Autowired
    VoucherRepository vcherRepo;
    @Autowired
    HoaDonRepository hoaDonRepository;


    public NormalTableResponse<VoucherHoaDon> getAll(NormalTableRequest request) {

        if (request.getPage() ==  null) {
            request.setPage(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getStatus() == null) {
            request.setStatus(1);
        }

        NormalTableResponse<VoucherHoaDon> voucherHoaDonNormalTableResponse = new NormalTableResponse<>();
        Pageable pageable = PageRequest.of(request.getPage() - 1,request.getPageSize());
        Page<VoucherHoaDon> voucherPage;
        if (request.getKeyWord() != null && vhRepo.searchByVoucherMa(request.getKeyWord(), pageable).hasContent()) {
            voucherPage = vhRepo.searchByVoucherMa(request.getKeyWord(), pageable);
        } else if (request.getKeyWord() != null && vhRepo.searchByVoucherTen(request.getKeyWord(), pageable).hasContent()) {
            voucherPage = vhRepo.searchByVoucherTen(request.getKeyWord(), pageable);
        }else {
            voucherPage = vhRepo.findAllByTrangThai(request.getStatus(), pageable);
        }

        List<VoucherHoaDon> voucherHoaDons = voucherPage.getContent().stream().toList();

        voucherHoaDonNormalTableResponse.setItem(voucherHoaDons);
        voucherHoaDonNormalTableResponse.setPage(request.getPage());
        voucherHoaDonNormalTableResponse.setPageSize(request.getPageSize());
        voucherHoaDonNormalTableResponse.setTotalItem(voucherPage.getTotalElements());

        return voucherHoaDonNormalTableResponse;
    }

    public VoucherHoaDon add(VoucherHoaDonRequest request) {
        System.out.println("addDC1");
        System.out.println(request);
        VoucherHoaDon voucherHoaDon = new VoucherHoaDon();
        voucherHoaDon.setNgayTao(LocalDateTime.now());
        voucherHoaDon.setVoucher(vcherRepo.searchById(request.getVoucher()).get());
        voucherHoaDon.setHoaDon(hoaDonRepository.searchById(request.getHoaDon()).get());
        voucherHoaDon.setTrangThai(1);

        return vhRepo.save(voucherHoaDon);
    }

    public VoucherHoaDon update(VoucherHoaDonRequest request) {
        VoucherHoaDon voucherHoaDon = vhRepo.findById(request.getId()).get();
        voucherHoaDon.setNgaySua(LocalDateTime.now());
        voucherHoaDon.setVoucher(vcherRepo.searchById(request.getVoucher()).get());
        voucherHoaDon.setHoaDon(hoaDonRepository.searchById(request.getHoaDon()).get());
        voucherHoaDon.setTrangThai(request.getTrangThai());
        return vhRepo.save(voucherHoaDon);
    }

    public VoucherHoaDon udateTrangThai(Long id, Integer status){
        VoucherHoaDon voucherHoaDon = vhRepo.findById(id).get();
        voucherHoaDon.setTrangThai(status);
        return vhRepo.save(voucherHoaDon);
    }
}
