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

    @Query("SELECT h FROM HopThoaiNhanVien h WHERE " +
            "(h.nhanVien1.id = :nhanVienId AND (:keyWord IS null OR h.nhanVien2.soDienThoai LIKE :keyWord OR h.nhanVien2.hoTen LIKE %:keyWord%))" +
            "OR h.nhanVien2.id = :nhanVienId AND (:keyWord IS null OR h.nhanVien1.soDienThoai LIKE :keyWord OR h.nhanVien1.hoTen LIKE %:keyWord%)")
    List<HopThoaiNhanVien> findByNhanVien(@Param("nhanVienId") Long nhanVienId, @Param("keyWord") String keyWord);

}
