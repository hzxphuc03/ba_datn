package datn.be.mycode.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class DanhGiaSanPhamRequest {

    private Long id;
    private int sao;
    private String cmt;
    private List<String> listHinhAnh;
    private int trangThai;
    private Long idKhachHang;
    private Long idSanPham;
}
