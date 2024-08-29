package datn.be.mycode.repository;

import datn.be.mycode.entity.TheLoai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Long> {

    Page<TheLoai> findByTen(String name, Pageable pageable);

    Page<TheLoai> findAllByTrangThai(int trangThai, Pageable pageable);

}
