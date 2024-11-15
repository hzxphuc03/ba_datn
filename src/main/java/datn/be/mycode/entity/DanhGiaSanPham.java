package datn.be.mycode.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "danh_gia_san_pham")
public class DanhGiaSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sao")
    private int sao;

    @Column(name = "cmt")
    private String cmt;

    @Column(name = "list_hinh_anh")
    private String listHinhAnh;

    @Column(name = "trang_thai")
    private int trangThai;

    @ManyToOne()
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang idKhachHang;

    @ManyToOne()
    @JoinColumn(name = "id_san_pham", referencedColumnName = "id")
    private SanPham idSanPham;

}
