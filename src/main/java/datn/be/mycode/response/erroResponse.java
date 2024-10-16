package datn.be.mycode.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class erroResponse {

    private int errorCode;

    private String messageError;
}
