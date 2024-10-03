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
@Table(name = "doan_chat")
public class DoanChat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tin_nhan_cuoi")
    private String tinNhan;

    @Column(name = "the_loai")
    private String theLoai;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_gui")
    private String nguoiGui;

    @Column(name = "nguoi_nhan")
    private String nguoiNhan;

    @Column(name = "trang_thai")
    private int trangThai;

    @ManyToOne()
    @JoinColumn(name = "id_hop_thoai", referencedColumnName = "id")
    private HopThoai hopThoai;

}
