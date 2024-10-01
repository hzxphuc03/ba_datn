package datn.be.mycode.repository;

import datn.be.mycode.entity.SanPham;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.ngayTao, sp.ngaySua, sp.soLuongDaBan, sp.nguoiTao, sp.trangThai, SUM(spct.soLuong))" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "WHERE " +
            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:startDate IS NULL OR sp.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR sp.ngayTao <= :endDate) AND " +
            "(:status IS NULL OR sp.trangThai = :status)" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.ngayTao, sp.ngaySua, sp.soLuongDaBan, sp.nguoiTao, sp.trangThai"
    )
    Page<SanPhamResponse> searchAll(String ten, LocalDateTime startDate, LocalDateTime endDate, Integer status, Pageable pageable);

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.ngayTao, sp.ngaySua, sp.soLuongDaBan, sp.nguoiTao, sp.trangThai, SUM(spct.soLuong))" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "WHERE " +
            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:startDate IS NULL OR sp.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR sp.ngayTao <= :endDate) AND " +
            "(:status IS NULL OR sp.trangThai = :status)" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.ngayTao, sp.ngaySua, sp.soLuongDaBan, sp.nguoiTao, sp.trangThai " +
            "HAVING SUM(spct.soLuong)<=10 " +
            "ORDER BY SUM(spct.soLuong) ASC"
    )
    Page<SanPhamResponse> searchAllSortedBySoLuong(String ten, LocalDateTime startDate, LocalDateTime endDate, Integer status, Pageable pageable);

}
