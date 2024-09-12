package datn.be.mycode.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SanPhamRequest {
    private Long id;
    private String ma;
    private String ten;
    private String moTa;
    private LocalDateTime ngayTao;
    private LocalDateTime NgaySua;
    private int soLuongDaBan;
    private String nguoiTao;
    private int trangThai;
}
