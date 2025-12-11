package datn.be.mycode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"JwtAuthenticationFilter.java:doFilterInternal\",\"message\":\"Request received\",\"data\":{\"method\":\"" + method + "\",\"path\":\"" + requestPath + "\",\"contextPath\":\"" + (contextPath != null ? contextPath : "null") + "\",\"servletPath\":\"" + (servletPath != null ? servletPath : "null") + "\",\"requestURL\":\"" + request.getRequestURL() + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        
        // Log incoming request for debugging - Use System.out for immediate visibility
        System.out.println("========================================");
        System.out.println("🔍 [JWT Filter] Processing: " + method + " " + requestPath);
        System.out.println("🔍 [JWT Filter] Context Path: " + (contextPath != null ? contextPath : "null"));
        System.out.println("🔍 [JWT Filter] Servlet Path: " + (servletPath != null ? servletPath : "null"));
        System.out.println("🔍 [JWT Filter] Full URL: " + request.getRequestURL());
        logger.info("🔍 [JWT Filter] Processing: " + method + " " + requestPath);
        
        // CRITICAL: Skip JWT validation for public endpoints (login, register, etc.)
        // Must return immediately without touching SecurityContext
        System.out.println("🔍 [JWT Filter] Checking if public endpoint - Path: [" + requestPath + "]");
        boolean isPublic = isPublicEndpoint(requestPath);
        System.out.println("🔍 [JWT Filter] Is public endpoint? " + isPublic + " (Path: [" + requestPath + "])");
        
        if (isPublic) {
            System.out.println("✅ [JWT Filter] PUBLIC ENDPOINT DETECTED - SKIPPING JWT VALIDATION");
            System.out.println("✅ [JWT Filter] Path: " + requestPath);
            System.out.println("✅ [JWT Filter] Continuing filter chain WITHOUT touching SecurityContext");
            logger.info("✅ [JWT Filter] Public endpoint - skipping JWT validation: " + requestPath);
            
            // CRITICAL: Do NOT touch SecurityContext for public endpoints
            // Just continue the filter chain
            filterChain.doFilter(request, response);
            
            System.out.println("✅ [JWT Filter] Filter chain completed for public endpoint");
            System.out.println("========================================");
            return;
        }
        
        // For protected endpoints, try to extract and validate JWT token
        try {
            String token = getTokenFromRequest(request);
            
            if (StringUtils.hasText(token)) {
                System.out.println("🔑 [JWT Filter] Token found, validating...");
                logger.info("🔑 [JWT Filter] Token found, validating...");
                
                // Validate token first
                boolean isValid = jwtTokenProvider.validateToken(token);
                System.out.println("🔑 [JWT Filter] Token validation result: " + isValid);
                
                if (isValid) {
                    // Get authentication from token
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    
                    if (authentication != null) {
                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("✅ [JWT Filter] Authentication set successfully");
                        System.out.println("✅ [JWT Filter] User: " + authentication.getName());
                        System.out.println("✅ [JWT Filter] Authorities: " + authentication.getAuthorities());
                        logger.info("✅ [JWT Filter] Authentication set for user: " + authentication.getName() + 
                                   " with authorities: " + authentication.getAuthorities());
                    } else {
                        System.err.println("⚠️ [JWT Filter] Token valid but authentication is null");
                        logger.warn("⚠️ [JWT Filter] Token valid but authentication is null");
                        // Clear SecurityContext if authentication is null
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    System.err.println("⚠️ [JWT Filter] Invalid token for path: " + requestPath);
                    logger.warn("⚠️ [JWT Filter] Invalid token for path: " + requestPath);
                    // Clear SecurityContext for invalid token
                    SecurityContextHolder.clearContext();
                }
            } else {
                // No token found - let Spring Security handle it (will check permitAll)
                System.out.println("ℹ️ [JWT Filter] No token found for protected endpoint: " + requestPath);
                logger.info("ℹ️ [JWT Filter] No token found for protected endpoint: " + requestPath);
                // DO NOT set SecurityContext - let Spring Security authorization check handle it
                // If endpoint requires auth, Spring Security will call AuthenticationEntryPoint
            }
        } catch (Exception ex) {
            // Log error but continue filter chain
            // DO NOT block request - let Spring Security handle authorization
            System.err.println("❌ [JWT Filter] Error processing token for path: " + requestPath);
            System.err.println("❌ [JWT Filter] Exception: " + ex.getMessage());
            ex.printStackTrace();
            logger.error("❌ [JWT Filter] Error processing token for path: " + requestPath, ex);
            SecurityContextHolder.clearContext();
        }
        
        // Continue filter chain - Spring Security will check authorization
        System.out.println("➡️ [JWT Filter] Continuing filter chain...");
        System.out.println("========================================");
        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path is a public endpoint that doesn't require JWT
     * CRITICAL: This must match SecurityConfig permitAll() patterns
     */
    private boolean isPublicEndpoint(String path) {
        if (path == null) {
            System.out.println("⚠️ [JWT Filter] Path is null!");
            return false;
        }
        
        // Trim và normalize path
        path = path.trim();
        
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"JwtAuthenticationFilter.java:isPublicEndpoint\",\"message\":\"Checking if path is public\",\"data\":{\"path\":\"" + path + "\",\"pathLength\":" + path.length() + "},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        
        System.out.println("🔍 [JWT Filter] isPublicEndpoint check - Path: [" + path + "], Length: " + path.length() + ", First 20 chars: [" + (path.length() > 20 ? path.substring(0, 20) : path) + "]");
        
        // Public auth endpoints - ALL /api/auth/** endpoints are public
        // This includes: /api/auth/login, /api/auth/login/**, /api/auth/register, /api/auth/register/**
        if (path.startsWith("/api/auth/")) {
            System.out.println("✅ [JWT Filter] Path is public auth endpoint: " + path);
            return true;
        }
        
        // Socket.IO endpoints - allow handshake requests (HTTP polling fallback)
        // Socket.IO server runs on separate port 9092, but HTTP polling requests may come to port 8080
        // Match both /socket.io and /socket.io/** patterns
        // Normalize path: remove trailing slash for comparison
        String normalizedPath = path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
        boolean isSocketIO = path.startsWith("/socket.io") || normalizedPath.equals("/socket.io");
        System.out.println("🔍 [JWT Filter] Socket.IO check - Path: [" + path + "], Normalized: [" + normalizedPath + "], startsWith('/socket.io'): " + path.startsWith("/socket.io") + ", equals('/socket.io'): " + normalizedPath.equals("/socket.io"));
        
        if (isSocketIO) {
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
                fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B\",\"location\":\"JwtAuthenticationFilter.java:isPublicEndpoint\",\"message\":\"Socket.IO path detected\",\"data\":{\"path\":\"" + path + "\",\"isPublic\":true},\"timestamp\":" + System.currentTimeMillis() + "}\n");
                fw.close();
            } catch (Exception e) {}
            // #endregion
            System.out.println("✅ [JWT Filter] Path is Socket.IO endpoint: " + path);
            System.out.println("ℹ️ [JWT Filter] Note: Socket.IO may be disabled on production. Request will be handled by SocketIOInfoController.");
            return true;
        }
        
        // Other public endpoints
        if (path.startsWith("/dang-ky") || path.startsWith("/quen-mat-khau")) {
            return true;
        }
        
        // Swagger endpoints
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") 
            || path.startsWith("/swagger-resources") || path.startsWith("/webjars")) {
            return true;
        }
        
        // Actuator health check
        if (path.startsWith("/actuator/health")) {
            return true;
        }
        
        // Spring Boot error handler
        if (path.equals("/error")) {
            System.out.println("✅ [JWT Filter] Path is error endpoint: " + path);
            return true;
        }
        
        return false;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken == null) {
            // Also check for lowercase 'authorization' header (some clients send it lowercase)
            bearerToken = request.getHeader("authorization");
            if (bearerToken != null) {
                System.out.println("🔍 [JWT Filter] Found lowercase 'authorization' header");
            } else {
                System.out.println("🔍 [JWT Filter] Authorization header: null");
            }
        } else {
            System.out.println("🔍 [JWT Filter] Authorization header present (length: " + bearerToken.length() + ")");
            System.out.println("🔍 [JWT Filter] Authorization header preview: " + 
                (bearerToken.length() > 30 ? bearerToken.substring(0, 30) + "..." : bearerToken));
        }
        
        if (bearerToken != null && StringUtils.hasText(bearerToken)) {
            // Remove "Bearer " prefix (case-insensitive)
            String token = bearerToken.trim();
            if (token.length() > 7 && token.substring(0, 7).equalsIgnoreCase("Bearer ")) {
                token = token.substring(7).trim();
                if (StringUtils.hasText(token)) {
                    System.out.println("✅ [JWT Filter] Token extracted successfully (length: " + token.length() + ")");
                    return token;
                } else {
                    System.err.println("⚠️ [JWT Filter] Token is empty after removing 'Bearer ' prefix");
                }
            } else {
                System.err.println("⚠️ [JWT Filter] Authorization header does not start with 'Bearer '");
                System.err.println("⚠️ [JWT Filter] Header value: " + (bearerToken.length() > 50 ? bearerToken.substring(0, 50) + "..." : bearerToken));
            }
        }
        
        System.out.println("ℹ️ [JWT Filter] No valid Bearer token found in request");
        return null;
    }
}

