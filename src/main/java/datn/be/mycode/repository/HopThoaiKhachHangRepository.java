package datn.be.mycode.repository;

import datn.be.mycode.entity.HopThoaiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HopThoaiKhachHangRepository extends JpaRepository<HopThoaiKhachHang, Long> {
}
