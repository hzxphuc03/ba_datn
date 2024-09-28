package datn.be.mycode.repository;

import datn.be.mycode.entity.ThaoTacSanPham;
import datn.be.mycode.entity.TheLoai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ThaoTacSanPhamRepository extends JpaRepository<ThaoTacSanPham,Long> {

    @Query("SELECT new datn.be.mycode.response.ThaoTacSanPhamResponse(sp.id, sp.thaoTac, sp.ngayTao, sp.trangThai)" +
            "FROM ThaoTacSanPham sp" +
            "LEFT JOIN NhanVien nv ON sp.id = nv.nhanVien.id " +
            "WHERE " +
            "(:startDate IS NULL OR sp.ngayTao >= :startDate) AND " +

            "(:status IS NULL OR sp.trangThai = :status)" +
            "GROUP BY sp.id,sp.ngayTao, sp.thaoTac, sp.trangThai"
    )
    Page<ThaoTacSanPham> findAllByTrangThai(int trangThai, Pageable pageable);


}
