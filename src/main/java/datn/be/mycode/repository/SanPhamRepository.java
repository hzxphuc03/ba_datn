package datn.be.mycode.repository;

import datn.be.mycode.entity.GiamGia;
import datn.be.mycode.entity.SanPham;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.request.sanPham.SanPhamByAllReq;
import datn.be.mycode.response.SanPhamResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai,sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe)" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "LEFT JOIN sp.giamGia g " +
            "WHERE " +
            "(:#{#request.keyWord} IS null OR sp.ten LIKE %:#{#request.keyWord}%) AND " +
            "(:#{#request.startDate} IS NULL OR sp.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR sp.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.giamGia} IS NULL OR g.id = :#{#request.giamGia}) AND " +
            "(:#{#request.thuongHieu} IS NULL OR sp.thuongHieu.id = :#{#request.thuongHieu}) AND " +
            "(:#{#request.theLoai} IS NULL OR sp.theLoai.id = :#{#request.theLoai}) AND " +
            "(:#{#request.loaiCo} IS NULL OR sp.loaiCo.id = :#{#request.loaiCo}) AND " +
            "(:#{#request.vatLieu} IS NULL OR sp.vatLieu.id = :#{#request.vatLieu}) AND " +
            "(:#{#request.loaiDe} IS NULL OR sp.loaiDe.id = :#{#request.loaiDe}) AND " +
            "(:#{#request.status} IS NULL OR sp.trangThai = :#{#request.status})" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai, sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe " +
            "ORDER BY sp.ngayTao DESC"
    )
    Page<SanPhamResponse> searchAll(@Param("request") TableSanPhamRequest request, Pageable pageable);

    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai, sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe)" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "LEFT JOIN sp.giamGia g " +
            "WHERE " +
            "(:#{#request.keyWord} IS null OR sp.ten LIKE %:#{#request.keyWord}%) AND " +
            "(:#{#request.startDate} IS NULL OR sp.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR sp.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.giamGia} IS NULL OR g.id = :#{#request.giamGia}) AND " +
            "(:#{#request.thuongHieu} IS NULL OR sp.thuongHieu.id = :#{#request.thuongHieu}) AND " +
            "(:#{#request.theLoai} IS NULL OR sp.theLoai.id = :#{#request.theLoai}) AND " +
            "(:#{#request.loaiCo} IS NULL OR sp.loaiCo.id = :#{#request.loaiCo}) AND " +
            "(:#{#request.vatLieu} IS NULL OR sp.vatLieu.id = :#{#request.vatLieu}) AND " +
            "(:#{#request.loaiDe} IS NULL OR sp.loaiDe.id = :#{#request.loaiDe}) AND " +
            "(:#{#request.status} IS NULL OR sp.trangThai = :#{#request.status})" +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai, sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe " +
            "ORDER BY sp.soLuong ASC"
    )
    Page<SanPhamResponse> searchAllSortedBySoLuong(@Param("request") TableSanPhamRequest request, Pageable pageable);


    @Query("SELECT new datn.be.mycode.response.SanPhamResponse(sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai,sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe)" +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
            "LEFT JOIN sp.giamGia g " +
            "WHERE " +
            "(:id IS null OR sp.id = :id) " +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.trangThai, sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe "
    )
    SanPhamResponse searchById(Long id);

    @Query("SELECT sp.id FROM SanPham sp JOIN SanPhamChiTiet spct ON spct.sanPham.id = sp.id " +
            "JOIN HoaDonChiTiet hdct ON hdct.sanPhamChiTiet.id = spct.id " +
            "GROUP BY sp.id ORDER BY SUM(hdct.soLuong) DESC")
    List<Long> top5SanPhamBanChay();

    @Query("SELECT sp.id FROM SanPham sp " +
            "JOIN sp.giamGia gg " +
            "WHERE gg.trangThai = 1 AND gg.ngayBatDau <= CURRENT_DATE AND gg.ngayKetThuc >= CURRENT_DATE " +
            "ORDER BY gg.mucGiam DESC")
    List<Long> topSanPhamGiamGia();

    @Query(value = "SELECT s FROM SanPham s ORDER BY s.ngayTao DESC")
    List<SanPham> top5SanPhamMoi(Pageable pageable);


    List<SanPham> findAllByGiamGiaId(Long idGiamGia);

    @Query("SELECT sp FROM SanPham sp " +
            "JOIN sp.giamGia gg " +
            "JOIN SanPhamChiTiet spct ON spct.sanPham = sp " +
            "WHERE gg.trangThai = 1 AND gg.ngayBatDau <= CURRENT_DATE AND gg.ngayKetThuc >= CURRENT_DATE " +
            "ORDER BY gg.mucGiam DESC, spct.giaBan ASC")
    List<SanPham> findSanPhamGiamGiaSauNhat(Pageable pageable);

    @Query("SELECT sp FROM SanPham sp ORDER BY sp.ngaySua DESC")
    List<SanPham> findTop12SanPhamByUpdatedDate();

    @Query("SELECT sp " +
            "FROM SanPham sp " +
            "WHERE " +
            "(:#{#request.keyWord} IS null OR sp.ten LIKE CONCAT('%', :#{#request.keyWord}, '%')) AND " +
            "(:#{#request.giaCaoNhat} IS NULL OR sp.giaThapNhat <= :#{#request.giaCaoNhat}) AND " +
            "(:#{#request.giaThapNhat} IS NULL OR sp.giaThapNhat >= :#{#request.giaThapNhat}) AND " +
            "(:#{#request.idGiamGia} IS NULL OR sp.giamGia.id = :#{#request.idGiamGia}) AND " +
            "(:#{#request.idThuongHieu} IS NULL OR sp.thuongHieu.id = :#{#request.idThuongHieu}) AND " +
            "(:#{#request.idTheLoai} IS NULL OR sp.theLoai.id = :#{#request.idTheLoai}) AND " +
            "(:#{#request.idLoaiCo} IS NULL OR sp.loaiCo.id = :#{#request.idLoaiCo}) AND " +
            "(:#{#request.idVatLieu} IS NULL OR sp.vatLieu.id = :#{#request.idVatLieu}) AND " +
            "(:#{#request.idLoaiDe} IS NULL OR sp.loaiDe.id = :#{#request.idLoaiDe}) AND " +
            "sp.trangThai = 1 " +
            "GROUP BY sp.id, sp.ma, sp.ten, sp.moTa, sp.hinhAnh, sp.trongLuong, sp.beMatSuDung, sp.congNghe, sp.kieuDang, sp.giaThapNhat, sp.giaCaoNhat, sp.ngayTao, sp.ngaySua, sp.nguoiTao, sp.soLuong, sp.trangThai, sp.giamGia, sp.thuongHieu, sp.theLoai, sp.loaiCo, sp.vatLieu, sp.loaiDe " +
            "ORDER BY " +
            "CASE WHEN :#{#request.sort} = 0 THEN sp.giaThapNhat END ASC, " +
            "CASE WHEN :#{#request.sort} = 1 THEN sp.giaThapNhat END DESC"
    )
    Page<SanPham> searchAllSortedByAll(SanPhamByAllReq request, Pageable pageable);
}
