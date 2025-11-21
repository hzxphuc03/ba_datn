package datn.be.mycode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter để log tất cả requests - giúp debug
 * Order = 1 để chạy TRƯỚC JWT Filter (Order mặc định)
 */
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = uri + (queryString != null ? "?" + queryString : "");
        
        System.out.println("========================================");
        System.out.println("📥 [RequestLogger] INCOMING REQUEST");
        System.out.println("📥 [RequestLogger] Method: " + method);
        System.out.println("📥 [RequestLogger] URI: " + uri);
        System.out.println("📥 [RequestLogger] Full URL: " + fullUrl);
        System.out.println("📥 [RequestLogger] Context Path: " + request.getContextPath());
        System.out.println("📥 [RequestLogger] Servlet Path: " + request.getServletPath());
        System.out.println("📥 [RequestLogger] Path Info: " + request.getPathInfo());
        System.out.println("========================================");
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            System.out.println("📤 [RequestLogger] RESPONSE SENT");
            System.out.println("📤 [RequestLogger] Status: " + response.getStatus());
            System.out.println("========================================");
        }
    }
}

