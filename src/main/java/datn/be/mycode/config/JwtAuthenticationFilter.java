package datn.be.mycode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        // Skip JWT filter cho các public endpoints
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = getTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Log lỗi nhưng vẫn tiếp tục filter chain
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Kiểm tra xem endpoint có phải là public endpoint không
     */
    private boolean isPublicEndpoint(String path) {
        // Public auth endpoints - không cần token
        if (path.startsWith("/api/auth/")) {
            return true;
        }
        
        // Public registration/forgot password endpoints
        if (path.startsWith("/dang-ky/") || path.startsWith("/quen-mat-khau/")) {
            return true;
        }
        
        // Swagger/OpenAPI endpoints
        if (path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/") 
            || path.startsWith("/swagger-resources/") || path.startsWith("/webjars/")) {
            return true;
        }
        
        // Actuator health check
        if (path.startsWith("/actuator/health")) {
            return true;
        }
        
        // WebSocket endpoints
        if (path.startsWith("/ws/") || path.startsWith("/app/") 
            || path.startsWith("/topic/") || path.startsWith("/user/")) {
            return true;
        }
        
        return false;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

