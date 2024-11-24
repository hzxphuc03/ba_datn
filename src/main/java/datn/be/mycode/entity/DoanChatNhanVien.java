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
@Table(name = "doan_chat_nhan_vien")
public class DoanChatNhanVien implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "the_loai")
    private String theLoai;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "duong_dan_ghi_am")
    private String voice;

    @Column(name = "tin_nhan", columnDefinition = "nvarchar(max)")
    private String tinNhan;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_dung_1")
    private NhanVien nguoiDung1;

    @Column(name = "nguoi_dung_2")
    private NhanVien nguoiDung2;

    @Column(name = "trang_thai")
    private int trangThai;

    @Column(name = "nguoi_gui")
    private boolean nguoiGui;

    @ManyToOne()
    @JoinColumn(name = "id_hop_thoai_nhan_vien", referencedColumnName = "id")
    private HopThoaiNhanVien hopThoaiNhanVien;

}
