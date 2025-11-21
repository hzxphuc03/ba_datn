package datn.be.mycode;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "DATN", version = "1.0")
)
public class MycodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycodeApplication.class, args);
    }
}
