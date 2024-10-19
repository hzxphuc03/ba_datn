package datn.be.mycode.repository;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.response.HoaDonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    @Query("SELECT hd " +
            "FROM HoaDon hd " +
            "WHERE " +
            "(:ma IS null OR hd.ma LIKE %:ma%) AND"+
            "(:startDate IS NULL OR hd.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR hd.ngayTao <= :endDate) AND " +
            "(:idKhachHang IS NULL OR hd.khachHang.id = :idKhachHang) AND " +
            "(:idNhanVien IS NULL OR hd.nhanVien.id = :idNhanVien)"
    )
    Page<HoaDon> searchAll(String ma, LocalDateTime startDate, LocalDateTime endDate, Long idKhachHang, Long idNhanVien, Pageable pageable);

    @Query("SELECT u FROM HoaDon u WHERE u.id = :id")
    Optional<HoaDon> searchById(@Param("id") Long id);
}
