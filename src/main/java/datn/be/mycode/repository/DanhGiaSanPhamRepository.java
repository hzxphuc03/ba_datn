package datn.be.mycode.repository;

import datn.be.mycode.entity.DanhGiaSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaSanPhamRepository extends JpaRepository<DanhGiaSanPham,Long> {

    Page<DanhGiaSanPham> findAllByTrangThai(int trangThai, Pageable pageable);

    @Query("SELECT d FROM DanhGiaSanPham d WHERE d.idSanPham.id = :idSanPham")
    List<DanhGiaSanPham> findByIdSanPham(@Param("idSanPham") Long idSanPham);


    @Query("SELECT l FROM DanhGiaSanPham l WHERE l.id  =:id")
    Page<DanhGiaSanPham> findAllById(@Param("id")Long id, Pageable pageable);

    @Query("SELECT d FROM DanhGiaSanPham d WHERE d.idKhachHang.id = :idKhachHang")
    Page<DanhGiaSanPham> findDanhGiaByKhachHangId(@Param("idKhachHang") Long idKhachHang, Pageable pageable);

    @Query("SELECT d FROM DanhGiaSanPham d WHERE d.idSanPham.id = :idSanPham ORDER BY d.ngayTao DESC")
    Page<DanhGiaSanPham> findDanhGiaBySanPhamId(@Param("idSanPham") Long idSanPham, Pageable pageable);


}
