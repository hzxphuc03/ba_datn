package datn.be.mycode.repository;

import datn.be.mycode.entity.SanPhamChiTiet;
import datn.be.mycode.request.customRequest.TableSanPhamChiTietRequest;
import datn.be.mycode.response.SanPhamChiTietResponse;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {

//    @Query("SELECT new datn.be.mycode.response.SanPhamChiTietResponse(spct.id, spct.ngayTao, spct.ngaySua, spct.hinhAnh, spct.giaHienHanh, spct.soLuong, spct.trangThai, spct.sanPham, spct.mauSac, spct.kichCo, spct.giamGia, spct.thuongHieu, spct.theLoai) " +
//            "FROM SanPhamChiTiet spct " +
//            "WHERE " +
////            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
//            "(:startDate IS NULL OR spct.ngayTao >= :startDate) AND " +
//            "(:endDate IS NULL OR spct.ngayTao <= :endDate) AND " +
//            "(:status IS NULL OR spct.trangThai = :status) AND " +
//            "(:sanPham IS NULL OR spct.sanPham.id = :sanPham) AND " +
//            "(:mauSac IS NULL OR spct.mauSac.id = :mauSac) AND " +
//            "(:kichCo IS NULL OR spct.kichCo.id = :kichCo) AND " +
////            "(:giamGia IS NULL OR spct.giamGia.id = :giamGia) AND " +
//            "(:thuongHieu IS NULL OR spct.thuongHieu.id = :thuongHieu) AND " +
//            "(:theLoai IS NULL OR spct.theLoai = :theLoai) "
//    )
//    Page<SanPhamChiTietResponse> searchAll( LocalDateTime startDate, LocalDateTime endDate, Integer status, Long sanPham, Long mauSac, Long kichCo, Long giamGia, Long thuongHieu, Long theLoai, Pageable pageable);


    @Query("SELECT new datn.be.mycode.response.SanPhamChiTietResponse(spct.id, spct.ngayTao, spct.ngaySua, spct.hinhAnh, spct.giaHienHanh, spct.soLuong, spct.trangThai, spct.sanPham, spct.mauSac, spct.kichCo, spct.thuongHieu, spct.theLoai) " +
            "FROM SanPhamChiTiet spct " +
            "WHERE " +
//            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:startDate IS NULL OR spct.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR spct.ngayTao <= :endDate) AND " +
            "(:status IS NULL OR spct.trangThai = :status) AND " +
            "(:sanPham IS NULL OR spct.sanPham.id = :sanPham) AND " +
            "(:mauSac IS NULL OR spct.mauSac.id = :mauSac) AND " +
            "(:kichCo IS NULL OR spct.kichCo.id = :kichCo) AND " +
//            "(:giamGia IS NULL OR spct.giamGia.id = :giamGia) AND " +
            "(:thuongHieu IS NULL OR spct.thuongHieu.id = :thuongHieu) AND " +
            "(:theLoai IS NULL OR spct.theLoai = :theLoai) "
    )
    Page<SanPhamChiTietResponse> searchAll( LocalDateTime startDate, LocalDateTime endDate, Integer status, Long sanPham, Long mauSac, Long kichCo, Long thuongHieu, Long theLoai, Pageable pageable);


    @Query("SELECT new datn.be.mycode.response.SanPhamChiTietResponse(spct.id, spct.ngayTao, spct.ngaySua, spct.hinhAnh, spct.giaHienHanh, spct.soLuong, spct.trangThai, spct.sanPham, spct.mauSac, spct.kichCo, spct.giamGia, spct.thuongHieu, spct.theLoai) " +
            "FROM SanPhamChiTiet spct " +
            "WHERE " +
//            "(:ten IS null OR sp.ten LIKE %:ten%) AND " +
            "(:startDate IS NULL OR spct.ngayTao >= :startDate) AND " +
            "(:endDate IS NULL OR spct.ngayTao <= :endDate) AND " +
            "(:status IS NULL OR spct.trangThai = :status) AND " +
            "(:sanPham IS NULL OR spct.sanPham.id = :sanPham) AND " +
            "(:mauSac IS NULL OR spct.mauSac.id = :mauSac) AND " +
            "(:kichCo IS NULL OR spct.kichCo.id = :kichCo) AND " +
//            "(:giamGia IS NULL OR spct.giamGia.id = :giamGia) AND " +
            "(:thuongHieu IS NULL OR spct.thuongHieu.id = :thuongHieu) AND " +
            "(:theLoai IS NULL OR spct.theLoai = :theLoai) AND "+
            "spct.soLuong <= 10 " +
            "ORDER BY spct.soLuong ASC"
    )
    Page<SanPhamChiTietResponse> searchAllSortedBySoLuong( LocalDateTime startDate, LocalDateTime endDate, Integer status, Long sanPham, Long mauSac, Long kichCo, Long thuongHieu, Long theLoai, Pageable pageable);

}
