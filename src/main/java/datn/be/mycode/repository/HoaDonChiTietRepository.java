package datn.be.mycode.repository;

import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    HoaDonChiTiet findByHoaDonIdAndSanPhamChiTietId(Long idHoaDon, Long idSanPhamChiTiet);

    List<HoaDonChiTiet> findAllBySanPhamChiTiet_IdAndTrangThai(Long idSanPhamChiTiet, int trangThai);

    List<HoaDonChiTiet> findAllByHoaDon_Id(Long idHoaDon);

    Page<HoaDonChiTiet> findAllByHoaDon_Id(Long idHoaDon, Pageable pageable);

    @Query("SELECT DISTINCT hdc.sanPhamChiTiet.sanPham FROM HoaDonChiTiet hdc " +
            "JOIN hdc.hoaDon hd " +
            "WHERE hdc.isCMT = false " +
            "AND hd.ngayThanhToan >= :startDate " +
            "AND hd.ngayThanhToan <= :endDate")
    List<SanPham> timSanPhamChuaCmtTrong7NgayThanhToan(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query("UPDATE HoaDonChiTiet hdc SET hdc.isCMT = true " +
            "WHERE hdc.hoaDon.khachHang.id = :idKhachHang " +
            "AND hdc.sanPhamChiTiet.sanPham.id = :idSanPham " +
            "AND hdc.isCMT = false")
    void updateIsCMTByKhachHangAndSanPham(@Param("idKhachHang") Long idKhachHang, @Param("idSanPham") Long idSanPham);

    @Query("SELECT hdc FROM HoaDonChiTiet hdc " +
            "WHERE hdc.hoaDon.khachHang.id = :idKhachHang AND hdc.isCMT = false")
    List<HoaDonChiTiet> timKhachHangIdCoIsCMTLaFalse(@Param("idKhachHang") Long idKhachHang);

    @Modifying
    @Query("UPDATE HoaDonChiTiet hdc SET hdc.isCMT = true " +
            "WHERE hdc.hoaDon.khachHang.id = :idKhachHang AND hdc.isCMT = false")
    void updateIsCMTThanhTrueTuKhachHangId(@Param("idKhachHang") Long idKhachHang);

}
