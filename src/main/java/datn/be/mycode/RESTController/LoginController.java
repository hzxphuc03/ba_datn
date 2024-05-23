package datn.be.mycode.RESTController;


import datn.be.mycode.config.JwtTokenProvider;
import datn.be.mycode.entity.loginTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;



    private static final List<loginTest> HARD_CODED_ACCOUNTS = new ArrayList<>();

    static {
        HARD_CODED_ACCOUNTS.add(new loginTest("admin", "123", "ADMIN"));
        HARD_CODED_ACCOUNTS.add(new loginTest("employee", "123", "EMPLOYEE"));
        HARD_CODED_ACCOUNTS.add(new loginTest("customer", "123", "CUSTOMER"));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody loginTest loginRequest) {
        for (loginTest user : HARD_CODED_ACCOUNTS) {
            if (user.getUsername().equals(loginRequest.getUsername()) && user.getPass().equals(loginRequest.getPass())) {
                String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
