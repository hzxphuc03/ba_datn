package datn.be.mycode.request;

import datn.be.mycode.entity.KhachHang;
import datn.be.mycode.entity.Voucher;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
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
