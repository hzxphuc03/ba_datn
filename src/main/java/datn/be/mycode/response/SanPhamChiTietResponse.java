package datn.be.mycode.response;

import datn.be.mycode.entity.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class SanPhamChiTietResponse {

    private Long id;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String hinhAnh;
    private BigDecimal giaHienHanh;
    private int soLuong;
    private int trangThai;
    private SanPham sanPham;
    private MauSac mauSac;
    private KichCo kichCo;
    private GiamGia giamGia;
    private ThuongHieu thuongHieu;
    private TheLoai theLoai;

    public SanPhamChiTietResponse(Long id, LocalDateTime ngayTao, LocalDateTime ngaySua, String hinhAnh, BigDecimal giaHienHanh, int soLuong, int trangThai, SanPham sanPham, MauSac mauSac, KichCo kichCo, ThuongHieu thuongHieu, TheLoai theLoai) {
        this.id = id;
        this.ngayTao = ngayTao;
        this.ngaySua = ngaySua;
        this.hinhAnh = hinhAnh;
        this.giaHienHanh = giaHienHanh;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
        this.sanPham = sanPham;
        this.mauSac = mauSac;
        this.kichCo = kichCo;
        this.giamGia = null;
        this.thuongHieu = thuongHieu;
        this.theLoai = theLoai;
    }
}
