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
public class HoaDonChiTietResponse {

    private Long id;
    private int soLuong;
    private BigDecimal giaHienHanh;
    private BigDecimal giaDaGiam;
    private int trangThai;
    private HoaDon hoaDon;
    private SanPhamChiTiet sanPhamChiTiet;

}
