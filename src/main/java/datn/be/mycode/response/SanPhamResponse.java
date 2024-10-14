package datn.be.mycode.response;


import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SanPhamResponse {
    private Long id;
    private String ma;
    private String ten;
    private String moTa;
    private String hinhAnh;
    private int trongLuong;
    private String beMatSuDung;
    private String congNghe;
    private String kieuDang;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private LocalDateTime ngayTao;
    private LocalDateTime NgaySua;
    private String nguoiTao;
    private Long soLuong;
    private int trangThai;
    private Long idGiamGia;
    private Long idThuongHieu;
    private Long idTheLoai;
    private Long idLoaiCo;
    private Long idVatLieu;
    private Long idLoaiDe;

}
