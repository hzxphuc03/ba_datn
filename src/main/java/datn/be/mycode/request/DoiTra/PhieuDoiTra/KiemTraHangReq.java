package datn.be.mycode.request.DoiTra.PhieuDoiTra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class KiemTraHangReq {
    private Long idPhieuDoiTra;
    private String video;
    private String ghiChu;
}
