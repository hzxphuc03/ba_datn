package datn.be.mycode.repository;



import datn.be.mycode.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    Page<Voucher> findAllByTrangThai(int trangThai, Pageable pageable);

    @Query("SELECT u FROM Voucher u WHERE u.ten LIKE %:query% OR u.ma LIKE %:query%")
    Page<Voucher> searchByTenOrMa(@Param("query") String query, Pageable pageable);


    @Query("SELECT u FROM Voucher u WHERE u.id = :id")
    Optional<Voucher> searchById(@Param("id") Long id);

    // Tìm kiếm theo khoảng ngày tạo
    @Query("SELECT v FROM Voucher v WHERE v.ngayTao BETWEEN :startDate AND :endDate")
    Page<Voucher> searchByNgayTao(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Tìm kiếm theo phần trăm giảm
    @Query("SELECT v FROM Voucher v WHERE v.phanTramGiam = :phanTramGiam")
    Page<Voucher> searchByPhanTramGiam(int phanTramGiam, Pageable pageable);
}
