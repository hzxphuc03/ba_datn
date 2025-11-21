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
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        
        // Log incoming request for debugging - Use System.out for immediate visibility
        System.out.println("========================================");
        System.out.println("🔍 [JWT Filter] Processing: " + method + " " + requestPath);
        System.out.println("🔍 [JWT Filter] Context Path: " + (contextPath != null ? contextPath : "null"));
        System.out.println("🔍 [JWT Filter] Servlet Path: " + (servletPath != null ? servletPath : "null"));
        System.out.println("🔍 [JWT Filter] Full URL: " + request.getRequestURL());
        logger.info("🔍 [JWT Filter] Processing: " + method + " " + requestPath);
        
        // CRITICAL: Skip JWT validation for public endpoints (login, register, etc.)
        // Must return immediately without touching SecurityContext
        boolean isPublic = isPublicEndpoint(requestPath);
        System.out.println("🔍 [JWT Filter] Is public endpoint? " + isPublic);
        
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
                logger.info("🔑 [JWT Filter] Token found, validating...");
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    if (authentication != null) {
                        // Only set authentication if token is valid
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("✅ [JWT Filter] Authentication set for user: " + authentication.getName());
                    } else {
                        logger.warn("⚠️ [JWT Filter] Token valid but authentication is null");
                        // Clear SecurityContext if authentication is null
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    logger.warn("⚠️ [JWT Filter] Invalid token for path: " + requestPath);
                    // Clear SecurityContext for invalid token
                    SecurityContextHolder.clearContext();
                }
            } else {
                // No token found - let Spring Security handle it (will check permitAll)
                logger.info("ℹ️ [JWT Filter] No token found for protected endpoint: " + requestPath);
                // DO NOT set SecurityContext - let Spring Security authorization check handle it
                // If endpoint requires auth, Spring Security will call AuthenticationEntryPoint
            }
        } catch (Exception ex) {
            // Log error but continue filter chain
            // DO NOT block request - let Spring Security handle authorization
            logger.error("❌ [JWT Filter] Error processing token for path: " + requestPath, ex);
            SecurityContextHolder.clearContext();
        }
        
        // Continue filter chain - Spring Security will check authorization
        String path = request.getServletPath();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        
        // Log tất cả request để debug
        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
        System.out.println("RequestURI: " + requestURI);
        System.out.println("Is Public: " + isPublicEndpoint(path));
        
        // Skip JWT filter cho các public endpoints
        if (isPublicEndpoint(path)) {
            System.out.println("✅ Skipping JWT filter for public endpoint: " + method + " " + path);
            logger.debug("Skipping JWT filter for public endpoint: " + method + " " + path);
            filterChain.doFilter(request, response);
            return;
        }
        
        System.out.println("⚠️ Processing JWT filter for: " + method + " " + path);
        
        // Chỉ xử lý JWT cho các request cần authentication
        try {
            String token = getTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("JWT authentication successful for: " + path);
                }
            } else {
                // Không có token hoặc token không hợp lệ - để Spring Security xử lý
                logger.debug("No valid JWT token for: " + method + " " + path);
            }
        } catch (Exception ex) {
            // Log lỗi nhưng vẫn tiếp tục filter chain để Spring Security xử lý
            logger.error("Could not set user authentication in security context for " + method + " " + path + ": " + ex.getMessage(), ex);
        }
        
        // Luôn tiếp tục filter chain - để Spring Security quyết định có cho phép không
        filterChain.doFilter(request, response);
    }
    
    /**
     * Kiểm tra xem endpoint có phải là public endpoint không
     */
    private boolean isPublicEndpoint(String path) {
        if (path == null) {
            System.out.println("❌ Path is null!");
            return false;
        }
        
        System.out.println("Checking if path is public: " + path);
        
        // Public auth endpoints - không cần token (quan trọng nhất!)
        if (path.startsWith("/api/auth/")) {
            System.out.println("✅ Path starts with /api/auth/ - PUBLIC");
            return true;
        }
        
        // Public registration/forgot password endpoints
        if (path.startsWith("/dang-ky") || path.startsWith("/quen-mat-khau")) {
            System.out.println("✅ Path is registration/forgot password - PUBLIC");
            return true;
        }
        
        // Swagger/OpenAPI endpoints
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") 
            || path.startsWith("/swagger-resources") || path.startsWith("/webjars")) {
            System.out.println("✅ Path is Swagger - PUBLIC");
            return true;
        }
        
        // Actuator health check
        if (path.startsWith("/actuator/health")) {
            System.out.println("✅ Path is Actuator health - PUBLIC");
            return true;
        }
        
        // WebSocket endpoints
        if (path.startsWith("/ws/") || path.startsWith("/app/") 
            || path.startsWith("/topic/") || path.startsWith("/user/")) {
            System.out.println("✅ Path is WebSocket - PUBLIC");
            return true;
        }
        
        System.out.println("❌ Path is NOT public - requires authentication");
        return false;
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
        
        // Public auth endpoints - ALL /api/auth/** endpoints are public
        // This includes: /api/auth/login, /api/auth/login/**, /api/auth/register, /api/auth/register/**
        if (path.startsWith("/api/auth/")) {
            System.out.println("✅ [JWT Filter] Path is public auth endpoint: " + path);
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
        if (bearerToken != null) {
            System.out.println("🔍 [JWT Filter] Authorization header: " + (bearerToken.length() > 20 ? bearerToken.substring(0, 20) + "..." : bearerToken));
        } else {
            System.out.println("🔍 [JWT Filter] Authorization header: null");
        }
        
        if (StringUtils.hasText(bearerToken) && bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println("🔍 [JWT Filter] Token extracted (length: " + token.length() + ")");
            return token;
        }
        
        System.out.println("🔍 [JWT Filter] No valid Bearer token found");
        return null;
    }
}

