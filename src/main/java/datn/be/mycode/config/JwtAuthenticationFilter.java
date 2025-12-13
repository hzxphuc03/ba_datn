package datn.be.mycode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Removed @Component - filter will be registered only in SecurityFilterChain
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Bỏ qua filter cho các public endpoints để tránh xung đột
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"JwtAuthenticationFilter.java:29\",\"message\":\"shouldNotFilter called\",\"data\":{\"path\":\"" + path + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        
        // Bỏ qua filter cho các public endpoints
        boolean shouldSkip = path.startsWith("/api/auth/") ||
                            path.startsWith("/dang-ky/") ||
                            path.startsWith("/quen-mat-khau/") ||
                            path.startsWith("/swagger-ui/") ||
                            path.startsWith("/v3/api-docs/") ||
                            path.startsWith("/swagger-resources/") ||
                            path.startsWith("/webjars/") ||
                            path.startsWith("/ws/") ||
                            path.startsWith("/app/") ||
                            path.startsWith("/topic/") ||
                            path.startsWith("/user/");
        
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"JwtAuthenticationFilter.java:48\",\"message\":\"shouldNotFilter result\",\"data\":{\"path\":\"" + path + "\",\"shouldSkip\":" + shouldSkip + "},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        
        if (shouldSkip) {
            logger.debug("Skipping JWT filter for public endpoint: " + path);
        }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"JwtAuthenticationFilter.java:52\",\"message\":\"doFilterInternal called\",\"data\":{\"path\":\"" + request.getServletPath() + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        
        try {
            String token = getTokenFromRequest(request);
            // #region agent log
            try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:66\",\"message\":\"token extracted\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"hasToken\":" + (token != null) + ",\"tokenLength\":" + (token != null ? token.length() : 0) + "},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
            // #endregion
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // #region agent log
                try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:69\",\"message\":\"token validated successfully\",\"data\":{\"path\":\"" + request.getServletPath() + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                // #endregion
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // #region agent log
                try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:72\",\"message\":\"authentication created\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"hasAuth\":" + (authentication != null) + ",\"authorities\":" + (authentication != null ? authentication.getAuthorities().toString() : "null") + "},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                // #endregion
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // #region agent log
                    try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:75\",\"message\":\"authentication set in SecurityContext\",\"data\":{\"path\":\"" + request.getServletPath() + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                    // #endregion
                }
            } else {
                // #region agent log
                try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:78\",\"message\":\"token validation failed or missing\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"hasToken\":" + (token != null) + ",\"isValid\":" + (token != null && jwtTokenProvider.validateToken(token)) + "},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                // #endregion
            }
        } catch (Exception ex) {
            // #region agent log
            try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:81\",\"message\":\"exception in filter\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"exception\":\"" + ex.getClass().getSimpleName() + "\",\"message\":\"" + ex.getMessage() + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
            // #endregion
            // Log lỗi nhưng vẫn tiếp tục filter chain
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"JwtAuthenticationFilter.java:84\",\"message\":\"getTokenFromRequest\",\"data\":{\"path\":\"" + request.getServletPath() + "\",\"hasHeader\":" + (bearerToken != null) + ",\"headerLength\":" + (bearerToken != null ? bearerToken.length() : 0) + ",\"startsWithBearer\":" + (bearerToken != null && bearerToken.startsWith("Bearer ")) + "},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        if (StringUtils.hasText(bearerToken) && bearerToken != null) {
            // Handle case where token might have "Bearer Bearer" prefix
            if (bearerToken.startsWith("Bearer Bearer ")) {
                return bearerToken.substring(14); // Skip "Bearer Bearer "
            } else if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7); // Skip "Bearer "
            }
        }
        return null;
    }
}

