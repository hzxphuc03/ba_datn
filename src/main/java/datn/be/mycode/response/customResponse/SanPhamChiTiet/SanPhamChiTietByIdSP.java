package datn.be.mycode.response.customResponse.SanPhamChiTiet;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class SanPhamChiTietByIdSP {

    private Long id;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String hinhAnh;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;
    private int soLuong;
    private int trangThai;
    private Long sanPham;
    private Long mauSac;
    private Long kichCo;
}
