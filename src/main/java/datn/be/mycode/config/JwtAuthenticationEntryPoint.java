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
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D\",\"location\":\"JwtAuthenticationEntryPoint.java:20\",\"message\":\"commence called\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"exception\":\"" + (authException != null ? authException.getClass().getSimpleName() : "null") + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Bạn cần đăng nhập để truy cập tài nguyên này");
        body.put("path", request.getServletPath());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}

