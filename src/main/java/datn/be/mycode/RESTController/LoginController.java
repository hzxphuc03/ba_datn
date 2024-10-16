package datn.be.mycode.RESTController;


import datn.be.mycode.config.JwtTokenProvider;
import datn.be.mycode.entity.loginTest;
import datn.be.mycode.response.LoginResponse;
import datn.be.mycode.response.erroResponse;
import datn.be.mycode.service.NhanVienService;
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
    @Autowired
    private NhanVienService nhanVienService;

    @PostMapping("/nhan_vien_login")
    public ResponseEntity<?> login(@RequestBody loginTest loginRequest) {
        LoginResponse loginResponse = nhanVienService.getAccount(loginRequest);
            if (loginResponse != null) {
                String token = jwtTokenProvider.generateToken(loginResponse.getUsername(), loginResponse.getRole());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new erroResponse(400,"Tài khoản hoặc mật khẩu không chính xác"));
    }

}
