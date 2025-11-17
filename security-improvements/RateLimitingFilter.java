package datn.be.mycode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_LOGIN_REQUESTS_PER_MINUTE = 5;
    
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static class RequestCounter {
        AtomicInteger count = new AtomicInteger(0);
        LocalDateTime windowStart = LocalDateTime.now();
        
        boolean isExpired() {
            return ChronoUnit.MINUTES.between(windowStart, LocalDateTime.now()) >= 1;
        }
        
        void reset() {
            count.set(0);
            windowStart = LocalDateTime.now();
        }
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String requestPath = request.getRequestURI();
        
        // Kiểm tra rate limit
        if (isRateLimited(clientIp, requestPath)) {
            sendRateLimitResponse(response);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isRateLimited(String clientIp, String requestPath) {
        String key = clientIp + ":" + (isLoginEndpoint(requestPath) ? "login" : "general");
        
        RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter());
        
        // Reset counter if window expired
        if (counter.isExpired()) {
            counter.reset();
        }
        
        int currentCount = counter.count.incrementAndGet();
        int maxRequests = isLoginEndpoint(requestPath) ? MAX_LOGIN_REQUESTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;
        
        return currentCount > maxRequests;
    }
    
    private boolean isLoginEndpoint(String requestPath) {
        return requestPath.contains("/api/auth/") && 
               (requestPath.contains("login") || requestPath.contains("register"));
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Too Many Requests");
        errorResponse.put("message", "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau.");
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}