package datn.be.mycode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MycodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycodeApplication.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(){
        return String.format("wellcome to code ","haha");
    }
}
