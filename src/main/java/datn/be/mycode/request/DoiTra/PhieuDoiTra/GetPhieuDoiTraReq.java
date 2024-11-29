package datn.be.mycode.request.DoiTra.PhieuDoiTra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GetPhieuDoiTraReq {

    private Integer page;

    private Integer pageSize;

    private String keyWord;

    private String maPhieuDoiTra;

    private Integer status;
}
