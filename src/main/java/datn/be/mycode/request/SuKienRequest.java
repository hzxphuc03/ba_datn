package datn.be.mycode.request;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SuKienRequest {
    private Long id;
    private String tenSuKien;
    private String maSuKien;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String keySuKien;
    private int trangThai;
}
