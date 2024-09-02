package datn.be.mycode.repository;

import datn.be.mycode.entity.GiamGia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiamGiaRepository extends JpaRepository<GiamGia,String> {
    Page<GiamGia> findByTen(String name, Pageable pageable);

    Page<GiamGia> findAllByTrangThai(int trangThai, Pageable pageable);


}
