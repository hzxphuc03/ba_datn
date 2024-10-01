package datn.be.mycode.response;


import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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
    private LocalDateTime ngayTao;
    private LocalDateTime NgaySua;
    private int soLuongDaBan;
    private String nguoiTao;
    private int trangThai;
    private Long soLuong;


}
