package datn.be.mycode.request;

import datn.be.mycode.entity.KhachHang;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DiaChiRequest {

    private Long id;
    @NotBlank(message = "ko dc trong")
    private String diaChi;
    private int trangThai;
    private Long idKhachHang;
}
