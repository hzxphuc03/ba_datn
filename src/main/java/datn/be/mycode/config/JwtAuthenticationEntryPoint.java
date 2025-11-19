package datn.be.mycode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        String path = request.getServletPath();
        
        // Không chặn các public endpoints
        if (isPublicEndpoint(path)) {
            // Cho phép request tiếp tục (không nên xảy ra nhưng để an toàn)
            return;
        }
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Bạn cần đăng nhập để truy cập tài nguyên này");
        body.put("path", path);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
    
    private boolean isPublicEndpoint(String path) {
        if (path == null) {
            return false;
        }
        // Public auth endpoints
        return path.startsWith("/api/auth/") 
            || path.startsWith("/dang-ky") 
            || path.startsWith("/quen-mat-khau")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/actuator/health");
    }
}

