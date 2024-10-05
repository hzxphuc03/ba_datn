package datn.be.mycode.repository;

import datn.be.mycode.entity.SanPham;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, SUM(spct.soLuong), sp.trangThai, sp.giamGia.id, sp.thuongHieu.id, sp.theLoai.id, sp.loaiCo.id, sp.vatLieu.id, sp.loaiDe.id)" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "WHERE " +
            "(:#{#request.keyWord} IS null OR sp.ten LIKE %:#{#request.keyWord}%) AND " +
            "(:#{#request.startDate} IS NULL OR sp.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR sp.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.status} IS NULL OR sp.trangThai = :#{#request.status})" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.trangThai, sp.giamGia.id, sp.thuongHieu.id, sp.theLoai.id, sp.loaiCo.id, sp.vatLieu.id, sp.loaiDe.id"
    )
    Page<SanPhamResponse> searchAll(@Param("request") TableSanPhamRequest request, Pageable pageable);

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, SUM(spct.soLuong), sp.trangThai, sp.giamGia.id, sp.thuongHieu.id, sp.theLoai.id, sp.loaiCo.id, sp.vatLieu.id, sp.loaiDe.id)" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "WHERE " +
            "(:#{#request.keyWord} IS null OR sp.ten LIKE %:#{#request.keyWord}%) AND " +
            "(:#{#request.startDate} IS NULL OR sp.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR sp.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.status} IS NULL OR sp.trangThai = :#{#request.status})" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.trangThai, sp.giamGia.id, sp.thuongHieu.id, sp.theLoai.id, sp.loaiCo.id, sp.vatLieu.id, sp.loaiDe.id " +
            "HAVING SUM(spct.soLuong)<=10 " +
            "ORDER BY SUM(spct.soLuong) ASC"
    )
    Page<SanPhamResponse> searchAllSortedBySoLuong(@Param("request") TableSanPhamRequest request, Pageable pageable);


}
