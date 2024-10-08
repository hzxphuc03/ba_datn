package datn.be.mycode.repository;

import datn.be.mycode.entity.LoaiDe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiDeRepository extends JpaRepository<LoaiDe,Long> {

//    Page<ThuongHieu> findByTen(String name, Pageable pageable);
//
//    Page<LoaiCo> findAllByTrangThai(int trangThai, Pageable pageable);
//
//    @Query("SELECT u FROM ThuongHieu u WHERE u.ten LIKE %:name%")
//    Page<ThuongHieu> searchByTen(String name, Pageable pageable);


}
