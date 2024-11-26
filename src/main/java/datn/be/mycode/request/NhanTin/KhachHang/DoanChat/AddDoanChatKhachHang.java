package datn.be.mycode.request.NhanTin.KhachHang.DoanChat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddDoanChatKhachHang {

    private String theLoai;
    private List<String> hinhAnhList;
    private String voice;
    private String tinNhan;
    private Long idNguoiDung1;
    private Long idNguoiDung2;
    private Integer trangThai;
    private Long idHopThoaiKhachHang;
}
