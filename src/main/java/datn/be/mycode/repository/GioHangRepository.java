package datn.be.mycode.repository;

import datn.be.mycode.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    Optional<GioHang> findByKhachHangId(Long idKhachHHang);
    
    // FIX: Return List to handle multiple carts, then take first active one
    List<GioHang> findAllByKhachHangIdOrderByNgayTaoDesc(Long idKhachHang);
    
    // Query to get the most recent active cart
    @Query("SELECT g FROM GioHang g WHERE g.khachHang.id = :idKhachHang AND g.trangThai = 1 ORDER BY g.ngayTao DESC")
    List<GioHang> findActiveCartsByKhachHangId(@Param("idKhachHang") Long idKhachHang);
}
