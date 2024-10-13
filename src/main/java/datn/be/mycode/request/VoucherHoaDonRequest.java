package datn.be.mycode.request;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.Voucher;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoucherHoaDonRequest {
    private Long id;
    @NotBlank(message = "ko dc trong")
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private int trangThai;
    private Long voucher;
    private Long hoaDon;
}
