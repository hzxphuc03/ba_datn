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
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"JwtAuthenticationEntryPoint.java:commence\",\"message\":\"401 Unauthorized triggered\",\"data\":{\"path\":\"" + requestPath + "\",\"method\":\"" + method + "\",\"exception\":\"" + (authException != null ? authException.getMessage() : "null") + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        
        // Log when entry point is called (should NOT happen for public endpoints)
        System.err.println("❌ [JWT EntryPoint] 401 Unauthorized - Path: " + method + " " + requestPath);
        System.err.println("❌ [JWT EntryPoint] Exception: " + (authException != null ? authException.getMessage() : "null"));
        
        // If this is called for /api/auth/** endpoints, it means SecurityConfig permitAll() is not working
        if (requestPath != null && requestPath.startsWith("/api/auth/")) {
            System.err.println("⚠️ [JWT EntryPoint] WARNING: EntryPoint called for auth endpoint! This should not happen!");
            System.err.println("⚠️ [JWT EntryPoint] Check SecurityConfig - /api/auth/** should be permitAll()");
            System.err.println("⚠️ [JWT EntryPoint] Request method: " + request.getMethod());
            System.err.println("⚠️ [JWT EntryPoint] Request URI: " + request.getRequestURI());
            System.err.println("⚠️ [JWT EntryPoint] Request URL: " + request.getRequestURL());
            System.err.println("⚠️ [JWT EntryPoint] Servlet path: " + request.getServletPath());
            System.err.println("⚠️ [JWT EntryPoint] Context path: " + request.getContextPath());
        }
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Bạn cần đăng nhập để truy cập tài nguyên này");
        body.put("path", requestPath);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}

