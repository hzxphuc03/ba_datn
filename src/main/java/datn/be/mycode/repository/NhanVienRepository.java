package datn.be.mycode.repository;

import datn.be.mycode.entity.NhanVien;
import datn.be.mycode.response.NhanVienResPonse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien,Long> {

        Page<NhanVien> findByHoTen(String name, Pageable pageable);

        Page<NhanVienResPonse> findAllByTrangThai(int trangThai, Pageable pageable);

    @Query("SELECT new datn.be.mycode.response.NhanVienResPonse(nv.id, nv.ma, nv.hoTen, nv.ngaySinh,nv.ngayTao,nv.soDienThoai,nv.gioiTinh,nv.diaChi,nv.hinhAnh,nv.email,nv.taiKhoan,nv.matKhau, nv.trangThai)" +
            "FROM NhanVien nv " +
            "LEFT JOIN ChucVu cv ON cv.id = nv.id " +
            "WHERE " +
            "(:ten IS null OR nv.hoTen LIKE %:ten%) AND " +
            "(:startDate IS NULL OR nv.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR nv.ngayTao <= :endDate) AND " +
            "(:status IS NULL OR nv.trangThai = :status)" +
            "GROUP BY  nv.hoTen, nv.ngaySinh,nv.ngayTao,nv.soDienThoai,nv.gioiTinh,nv.diaChi,nv.hinhAnh,nv.email,nv.taiKhoan,nv.matKhau, nv.trangThai"
    )
    Page<NhanVien> searchAll(String ten, LocalDateTime startDate, LocalDateTime endDate, Integer status, Pageable pageable);


}
