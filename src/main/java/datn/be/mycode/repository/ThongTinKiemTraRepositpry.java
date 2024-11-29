package datn.be.mycode.repository;

import datn.be.mycode.entity.ThongTinKiemTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongTinKiemTraRepositpry extends JpaRepository<ThongTinKiemTra, Long> {
}
