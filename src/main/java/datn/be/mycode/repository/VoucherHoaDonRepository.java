package datn.be.mycode.repository;

import datn.be.mycode.entity.VoucherHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherHoaDonRepository extends JpaRepository<VoucherHoaDon,Long> {

    Page<VoucherHoaDon> findAllByTrangThai(int trangThai, Pageable pageable);

    @Query("SELECT vhd FROM VoucherHoaDon vhd WHERE vhd.voucher.ma LIKE %:ma%")
    Page<VoucherHoaDon> searchByVoucherMa(String ma, Pageable pageable);

    @Query("SELECT vhd FROM VoucherHoaDon vhd WHERE vhd.voucher.ten LIKE %:ten%")
    Page<VoucherHoaDon> searchByVoucherTen(String ten, Pageable pageable);

    List<VoucherHoaDon> findByHoaDonId(Long idHoaDon);

    @Query("SELECT v FROM VoucherHoaDon v WHERE v.trangThai = 1")
    Page<VoucherHoaDon> findAllByTrangThai1(Pageable pageable);

    @Query("SELECT v FROM VoucherHoaDon v WHERE v.trangThai = 2")
    Page<VoucherHoaDon> findAllByTrangThai2(Pageable pageable);

}
