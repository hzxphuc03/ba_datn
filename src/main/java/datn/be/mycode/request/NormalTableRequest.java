package datn.be.mycode.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class NormalTableRequest {

    private int page;

    private int pageSize;

    private String keyWord;

    private int status;
}
