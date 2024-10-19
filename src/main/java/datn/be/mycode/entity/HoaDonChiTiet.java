package datn.be.mycode.entity;

import datn.be.mycode.request.HoaDonChiTietRequest;
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
@Builder

@Entity
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "gia_hien_hanh")
    private BigDecimal giaHienHanh;

    @Column(name = "gia_da_giam")
    private BigDecimal giaDaGiam;

    @Column(name = "trang_thai")
    private int trangThai;

    @ManyToOne()
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id")
    private HoaDon hoaDon;

    @ManyToOne()
    @JoinColumn(name = "id_chi_tiet_san_pham", referencedColumnName = "id")
    private SanPhamChiTiet sanPhamChiTiet;

    public void AddSoluong(int sl){
        soLuong = sl;
    }
}
