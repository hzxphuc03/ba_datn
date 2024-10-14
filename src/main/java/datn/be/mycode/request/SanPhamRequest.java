package datn.be.mycode.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SanPhamRequest {
    private Long id;
    private String ma;
    private String ten;
    private String moTa;
    private String hinhAnh;
    private int trongLuong;
    private String beMatSuDung;
    private String congNghe;
    private String kieuDang;
    private LocalDateTime ngayTao;
    private LocalDateTime NgaySua;
    private String nguoiTao;
    private int trangThai;

    private Long idGiamGia;
    private Long idThuongHieu;
    private Long idTheLoai;
    private Long idLoaiCo;
    private Long idVatLieu;
    private Long idLoaiDe;

}