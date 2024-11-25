package datn.be.mycode.repository;

import datn.be.mycode.entity.DoanChatKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoanChatKhachHangRepository extends JpaRepository<DoanChatKhachHang, Long> {
}
