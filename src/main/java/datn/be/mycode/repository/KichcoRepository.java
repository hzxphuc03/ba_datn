package datn.be.mycode.repository;

import datn.be.mycode.entity.KichCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KichcoRepository extends JpaRepository<KichCo, Long>  {
     Page<KichCo> findByTen(String name, Pageable pageable);

    Page<KichCo> findAllByTrangThai(int trangThai, Pageable pageable);


}
