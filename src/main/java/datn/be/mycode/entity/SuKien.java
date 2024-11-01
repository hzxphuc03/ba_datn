package datn.be.mycode.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "su-kien")
public class SuKien implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_su_kien")
    private String tenSuKien;

    @Column(name = "ma_su_kien")
    private String maSuKien;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "key_su_kien")
    private String keySuKien;

    @Column(name = "trang_thai")
    private int trangThai;
}
