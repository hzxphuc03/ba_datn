package datn.be.mycode.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "chi_tiet_san_pham")
public class SanPhamChiTiet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "gia_hien_hanh")
    private BigDecimal giaHienHanh;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "trang_thai")
    private int trangThai;

    @ManyToOne()
    @JoinColumn(name = "id_san_pham", referencedColumnName = "id")
    private SanPham sanPham;

    @ManyToOne()
    @JoinColumn(name = "id_mau_sac", referencedColumnName = "id")
    private MauSac mauSac;

    @ManyToOne()
    @JoinColumn(name = "id_kich_co", referencedColumnName = "id")
    private KichCo kichCo;

    @ManyToOne()
    @JoinColumn(name = "id_giam_gia", referencedColumnName = "id")
    private GiamGia giamGia;

    @ManyToOne()
    @JoinColumn(name = "id_thuong_hieu", referencedColumnName = "id")
    private ThuongHieu thuongHieu;

    @ManyToOne()
    @JoinColumn(name = "id_the_loai", referencedColumnName = "id")
    private TheLoai theLoai;

}
