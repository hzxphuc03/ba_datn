package datn.be.mycode.repository;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.HoaDonChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    HoaDonChiTiet findByHoaDonIdAndSanPhamChiTietId(Long idHoaDon, Long idSanPhamChiTiet);

    List<HoaDonChiTiet> findAllByHoaDon_Id(Long idHoaDon);

    Page<HoaDonChiTiet> findAllByHoaDon_Id(Long idHoaDon, Pageable pageable);
}
