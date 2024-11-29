package datn.be.mycode.repository;

import datn.be.mycode.entity.SanPhamDoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamDoiTraRepository extends JpaRepository<SanPhamDoiTra, Long> {
}
