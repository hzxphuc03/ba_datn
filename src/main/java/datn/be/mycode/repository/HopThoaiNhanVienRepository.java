package datn.be.mycode.repository;

import datn.be.mycode.entity.HopThoaiKhachHang;
import datn.be.mycode.entity.HopThoaiNhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HopThoaiNhanVienRepository extends JpaRepository<HopThoaiNhanVien, Long> {

    @Query("SELECT h FROM HopThoaiNhanVien h WHERE h.nhanVien1.id = :nhanVienId OR h.nhanVien2.id = :nhanVienId")
    List<HopThoaiNhanVien> findByNhanVien(@Param("nhanVienId") Long nhanVienId);
}
