package datn.be.mycode.request.customRequest;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TableDiaChiRequest {
    private Integer page;

    private Integer pageSize;

    private Long keyWord;

    private Integer status;
}
