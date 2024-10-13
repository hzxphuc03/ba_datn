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

    @Query("SELECT new datn.be.mycode.response.HoaDonResponse(hd.id, hd.ma, hd.ngayTao, hd.ngayThanhToan, hd.ghiChu, hd.tenNguoiNhan, hd.sdtNguoiNhan, hd.diaChiNguoiNhan, hd.phiShip, hd.tongTien, hd.tongTienSanPhamChuaGiam, hd.tongTienGiam, hd.phuongThucThanhToan, hd.nhanVien, hd.khachHang, hd.trangThai)" +
            "FROM HoaDon hd " +
            "WHERE " +
            "(:ma IS null OR hd.ma LIKE %:ma%) AND " +
            "(:startDate IS NULL OR hd.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR hd.ngayTao <= :endDate) AND " +
            "(:idKhachHang IS NULL OR hd.khachHang.id = :idKhachHang) AND " +
            "(:idNhanVien IS NULL OR hd.nhanVien.id = :idNhanVien)"
    )
    Page<HoaDonResponse> searchAll(String ma, LocalDateTime startDate, LocalDateTime endDate, Long idKhachHang, Long idNhanVien, Pageable pageable);

    @Query("SELECT u FROM HoaDon u WHERE u.id = :id")
    Optional<HoaDon> searchById(@Param("id") Long id);
}
