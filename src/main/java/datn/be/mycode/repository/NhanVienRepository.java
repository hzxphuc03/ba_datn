package datn.be.mycode.repository;

import datn.be.mycode.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien,Long> {

        Page<NhanVien> findByHoTen(String name, Pageable pageable);

        Page<NhanVien> findAllByTrangThai(int trangThai, Pageable pageable);

    }
