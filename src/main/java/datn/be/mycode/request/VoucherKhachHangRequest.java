package datn.be.mycode.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoucherKhachHangRequest {
    private Long id;
    @NotBlank(message = "ko dc trong")
    private Date ngayTao;
    private Date ngaySua;
    private int trangThai;
    private Long voucher;
    private Long khachHang;
}
