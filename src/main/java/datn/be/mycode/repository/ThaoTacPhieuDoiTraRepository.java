package datn.be.mycode.repository;

import datn.be.mycode.entity.PhieuDoiTra;
import datn.be.mycode.entity.ThaoTacPhieuDoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThaoTacPhieuDoiTraRepository extends JpaRepository<ThaoTacPhieuDoiTra, Long> {
}
