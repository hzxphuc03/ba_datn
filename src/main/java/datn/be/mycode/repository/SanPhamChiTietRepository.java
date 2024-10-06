package datn.be.mycode.repository;

import datn.be.mycode.entity.SanPhamChiTiet;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.request.customRequest.TableSanPhamRequest;
import datn.be.mycode.response.SanPhamChiTietResponse;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {

    @Query("SELECT new datn.be.mycode.response.SanPhamChiTietResponse(spct.id, spct.ngayTao, spct.ngaySua, spct.hinhAnh, spct.giaNhap, spct.giaBan, spct.soLuong, spct.trangThai, spct.sanPham, spct.mauSac, spct.kichCo) " +
            "FROM SanPhamChiTiet spct " +
            "WHERE " +
//            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:#{#request.startDate} IS NULL OR spct.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR spct.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.status} IS NULL OR spct.trangThai = :#{#request.status}) AND " +
            "(:#{#request.sanPham} IS NULL OR spct.sanPham.id = :#{#request.sanPham}) AND " +
            "(:#{#request.mauSac} IS NULL OR spct.mauSac.id = :#{#request.mauSac}) AND " +
            "(:#{#request.kichCo} IS NULL OR spct.kichCo.id = :#{#request.kichCo})  "

    )
    Page<SanPhamChiTietResponse> searchAll(@Param("request") TableSanPhamChiTietRequest request, Pageable pageable);


    @Query("SELECT new datn.be.mycode.response.SanPhamChiTietResponse(spct.id, spct.ngayTao, spct.ngaySua, spct.hinhAnh, spct.giaNhap, spct.giaBan, spct.soLuong, spct.trangThai, spct.sanPham, spct.mauSac, spct.kichCo) " +
            "FROM SanPhamChiTiet spct " +
            "WHERE " +
//            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:#{#request.startDate} IS NULL OR spct.ngayTao >= :#{#request.startDate}) AND " +
            "(:#{#request.endDate} IS NULL OR spct.ngayTao <= :#{#request.endDate}) AND " +
            "(:#{#request.status} IS NULL OR spct.trangThai = :#{#request.status}) AND " +
            "(:#{#request.sanPham} IS NULL OR spct.sanPham.id = :#{#request.sanPham}) AND " +
            "(:#{#request.mauSac} IS NULL OR spct.mauSac.id = :#{#request.mauSac}) AND " +
            "(:#{#request.kichCo} IS NULL OR spct.kichCo.id = :#{#request.kichCo})  AND " +
            "spct.soLuong <= 10 " +
            "ORDER BY spct.soLuong ASC"
    )
    Page<SanPhamChiTietResponse> searchAllSortedBySoLuong(@Param("request") TableSanPhamChiTietRequest request, Pageable pageable);


    List<SanPhamChiTiet> findAllBySanPham_Id(Long idSanPham);

}
