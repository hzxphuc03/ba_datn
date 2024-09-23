package datn.be.mycode.repository;

import datn.be.mycode.entity.DiaChi;
import datn.be.mycode.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {
    Page<DiaChi> findAllByTrangThai(int trangThai, Pageable pageable);

    @Query("SELECT u FROM DiaChi u WHERE u.diaChi LIKE %:name%")
    Page<DiaChi> searchByDiaChi(String name, Pageable pageable);
}
