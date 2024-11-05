package datn.be.mycode.request.gioHangChiTIet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetAll {

    private Integer page;

    private Integer pageSize;

    private Long idKhachHang;

    private Integer status;

}
