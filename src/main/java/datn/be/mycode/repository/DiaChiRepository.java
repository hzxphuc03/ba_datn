package datn.be.mycode.repository;

import datn.be.mycode.entity.DiaChi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {


    Page<DiaChi> findAllByTrangThai(int trangThai, Pageable pageable);


    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.id = :id")
    List<DiaChi> searchByIdKhachHang(@Param("id") Long id);

    @Modifying
    @Query("UPDATE DiaChi d SET d.trangThai = 0 WHERE d.khachHang.id = :khachHangId AND d.id <> :diaChiId")
    void updateTrangThaiKhac(@Param("khachHangId") Long khachHangId, @Param("diaChiId") Long diaChiId);

//    @Query("SELECT d.id FROM DiaChi d WHERE d.id = :id ")
    Page<DiaChi> findAllById( @Param("id")Long id,Pageable pageable);

}
