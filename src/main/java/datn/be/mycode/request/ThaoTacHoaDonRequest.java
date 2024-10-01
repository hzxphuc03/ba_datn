package datn.be.mycode.request;


import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.NhanVien;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ThaoTacHoaDonRequest {

    private Long id;

    private String thaoTac;

    private LocalDateTime ngayTao;

    private int trangThai;

    private Long idHoaDon;

    private Long idNhanVien;

}
