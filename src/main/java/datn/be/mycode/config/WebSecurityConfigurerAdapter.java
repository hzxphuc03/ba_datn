package datn.be.mycode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    // Đọc CORS allowed origins từ environment variable hoặc application.properties
    // Format: "http://localhost:4200,https://yourdomain.com"
    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    public WebSecurityConfigurerAdapter(JwtAuthenticationFilter jwtAuthenticationFilter,
                                       JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Đọc allowed origins từ config
        // Nếu là "*" thì dùng allowedOriginPatterns, nếu không thì dùng allowedOrigins
        if ("*".equals(allowedOrigins) || allowedOrigins == null || allowedOrigins.isEmpty()) {
            // Development: allow all origins with pattern
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        } else {
            // Production: list specific origins
            String[] originsArray = allowedOrigins.split(",");
            List<String> origins = new java.util.ArrayList<>();
            for (String origin : originsArray) {
                origins.add(origin.trim());
            }
            configuration.setAllowedOrigins(origins);
        }
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // CRITICAL: allowCredentials = true with allowedOriginPatterns("*") should work
        // But if you get error, you can either:
        // 1. Set allowCredentials = false (no credentials needed for JWT in Authorization header)
        // 2. List specific origins instead of "*"
        // For JWT authentication, credentials are not required (token is in Authorization header, not cookie)
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Single SecurityFilterChain - Simplified approach
     * Public endpoints are configured with permitAll() and processed first
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("🔧 [SecurityConfig] ========================================");
        System.out.println("🔧 [SecurityConfig] Building SecurityFilterChain...");
        
        // CRITICAL: Disable default security for public endpoints
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CRITICAL: Disable form login and http basic auth
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests((authorize) -> {
                    System.out.println("🔧 [SecurityConfig] Configuring authorization rules...");
                    // ========================================
                    // CRITICAL: PUBLIC ENDPOINTS - MUST BE FIRST
                    // ========================================
                    // ALL /api/auth/** endpoints are PUBLIC (no JWT required)
                    // This includes:
                    // - /api/auth/login
                    // - /api/auth/login/nhan-vien
                    // - /api/auth/login/khach-hang
                    // - /api/auth/register
                    // - /api/auth/refresh-token
                    // - /api/auth/** (any other auth endpoints)
                    authorize
                        // ========================================
                        // CRITICAL: PUBLIC ENDPOINTS - MUST BE FIRST
                        // ========================================
                        // Root path - allow for health checks and browser access
                        .requestMatchers("/", "/favicon.ico").permitAll()
                        
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Other public endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", 
                                       "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/dang-ky/**", "/quen-mat-khau/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // CRITICAL: Allow /error endpoint (Spring Boot error handler)
                        .requestMatchers("/error").permitAll()
                        
                        // WebSocket endpoints - yêu cầu authentication
                        // Authentication sẽ được xử lý trong WebSocketAuthInterceptor
                        // Cho phép handshake nhưng sẽ validate token trong interceptor
                        .requestMatchers("/ws/**").permitAll() // Cho phép handshake, nhưng validate trong interceptor
                        // Socket.IO endpoints - allow handshake requests (HTTP polling fallback)
                        // Socket.IO server runs on separate port 9092, but HTTP polling requests may come to port 8080
                        // CRITICAL: Match both /socket.io and /socket.io/** patterns
                        .requestMatchers("/socket.io", "/socket.io/**").permitAll() // Allow Socket.IO handshake/polling requests
                        // Các endpoint này sẽ được bảo vệ bởi WebSocketAuthInterceptor
                        // .requestMatchers("/app/**").authenticated()
                        // .requestMatchers("/topic/**").authenticated()
                        // .requestMatchers("/user/**").authenticated()
                        
                        // Public product/category endpoints - chỉ GET
                        .requestMatchers("GET", "/api/the_loai/**").permitAll()
                        .requestMatchers("GET", "/api/san_pham/**").permitAll()
                        .requestMatchers("GET", "/api/san_pham_chi_tiet/**").permitAll()
                        .requestMatchers("GET", "/api/su_kien/**").permitAll()
                        .requestMatchers("GET", "/api/thuong_hieu/**").permitAll()
                        .requestMatchers("GET", "/api/mau_sac/**").permitAll()
                        .requestMatchers("GET", "/api/danh_gia_san_pham/**").permitAll()
                        .requestMatchers("GET", "/api/voucher/**").permitAll()
                        .requestMatchers("GET", "/api/giam_gia/**").permitAll()
                        
                        // Auto update endpoints - public (scheduled jobs)
                        .requestMatchers("PUT", "/api/voucher/update-trangthai-auto").permitAll()
                        
                        // Web pages (JSP)
                        .requestMatchers("/WEB-INF/view/**", "/checkotp**", 
                                       "/otp**", "/success**", "/dangky**").permitAll()
                        
                        // Admin only endpoints
                        .requestMatchers("/api/admin/**", "/api/restAPI/**").hasRole("ADMIN")
                        .requestMatchers("/api/chuc_vu/**").hasRole("ADMIN")
                        .requestMatchers("/api/thong_ke/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        
                        // Admin và Nhân viên
                        .requestMatchers("/api/nhan_vien/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/hoa_don/**").hasAnyRole("ADMIN", "NHAN_VIEN", "KHACH_HANG")
                        .requestMatchers("/api/nhan_tin/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/doi_tra/**").hasAnyRole("ADMIN", "NHAN_VIEN", "KHACH_HANG")
                        
                        // Khách hàng endpoints - GET cho phép tạm thời để test, POST/PUT/DELETE cần auth
                        .requestMatchers("GET", "/api/khach_hang/**").permitAll() // Tạm thời public để test
                        .requestMatchers("POST", "/api/khach_hang/**", "PUT", "/api/khach_hang/**", 
                                       "DELETE", "/api/khach_hang/**").hasAnyRole("ADMIN", "NHAN_VIEN", "KHACH_HANG")
                        
                        // Khách hàng endpoints
                        .requestMatchers("/api/gio_hang/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/dia_chi/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/danh_gia/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/thong_bao/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/voucher_khach_hang/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        
                        // Image upload endpoints - require authentication
                        .requestMatchers("/api/upload/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        // Storage gateway proxy endpoints - require authentication
                        .requestMatchers("/api/storage-gateway/**").hasAnyRole("KHACH_HANG", "ADMIN", "NHAN_VIEN")
                        
                        // Quản lý sản phẩm - Admin và Nhân viên (POST, PUT, DELETE)
                        .requestMatchers("POST", "/api/the_loai/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("PUT", "/api/the_loai/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("DELETE", "/api/the_loai/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("/api/kich_co/**", "/api/vat_lieu/**", 
                                       "/api/loai_co/**", "/api/loai_de/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("POST", "/api/thuong_hieu/**", "/api/mau_sac/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("PUT", "/api/thuong_hieu/**", "/api/mau_sac/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("DELETE", "/api/thuong_hieu/**", "/api/mau_sac/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        
                        // Voucher - POST/PUT/DELETE cần admin (GET đã public ở trên)
                        .requestMatchers("POST", "/api/voucher/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("PUT", "/api/voucher/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("DELETE", "/api/voucher/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        
                        // Giam gia - POST/PUT/DELETE cần admin (GET đã public ở trên)
                        .requestMatchers("POST", "/api/giam_gia/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("PUT", "/api/giam_gia/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("DELETE", "/api/giam_gia/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("POST", "/api/su_kien/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("PUT", "/api/su_kien/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        .requestMatchers("DELETE", "/api/su_kien/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                        
                        // Tất cả các request còn lại cần authentication
                        .anyRequest().authenticated();
                    System.out.println("✅ [SecurityConfig] /api/auth/** -> permitAll()");
                    System.out.println("✅ [SecurityConfig] Authorization rules configured");
                    System.out.println("✅ [SecurityConfig] /api/auth/** is PUBLIC (permitAll)");
                })
                .exceptionHandling(ex -> {
                    System.out.println("🔧 [SecurityConfig] Configuring exception handling...");
                    ex.authenticationEntryPoint((request, response, authException) -> {
                        String path = request.getRequestURI();
                        String originalPath = (String) request.getAttribute("jakarta.servlet.error.request_uri");
                        String method = request.getMethod();
                        
                        
                        System.err.println("========================================");
                        System.err.println("❌ [SecurityConfig] AuthenticationEntryPoint called!");
                        System.err.println("❌ [SecurityConfig] Method: " + method);
                        System.err.println("❌ [SecurityConfig] Path: " + path);
                        System.err.println("❌ [SecurityConfig] Original Path: " + (originalPath != null ? originalPath : "null"));
                        System.err.println("❌ [SecurityConfig] Exception: " + (authException != null ? authException.getMessage() : "null"));
                        System.err.println("❌ [SecurityConfig] Request URL: " + request.getRequestURL());
                        System.err.println("❌ [SecurityConfig] Servlet Path: " + request.getServletPath());
                        System.err.println("❌ [SecurityConfig] Context Path: " + request.getContextPath());
                        
                        // Check if this is a Socket.IO request
                        if (path != null && path.startsWith("/socket.io")) {
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] CRITICAL: EntryPoint called for /socket.io endpoint!");
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] This means permitAll() is NOT working for Socket.IO!");
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] Path: " + path);
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] Check if pattern matches correctly!");
                        }
                        
                        // If this is called for /api/auth/**, it's a problem!
                        if (path != null && path.startsWith("/api/auth/")) {
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] CRITICAL: EntryPoint called for /api/auth/** endpoint!");
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] This means permitAll() is NOT working!");
                        }
                        
                        // If path is /error, the original request was blocked
                        if (path != null && path.equals("/error")) {
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] WARNING: EntryPoint called for /error endpoint!");
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] This means the ORIGINAL request was blocked!");
                            System.err.println("⚠️⚠️⚠️ [SecurityConfig] Check if the original endpoint is in permitAll()");
                        }
                        System.err.println("========================================");
                        
                        jwtAuthenticationEntryPoint.commence(request, response, authException);
                    });
                })
                .addFilterBefore(jwtAuthenticationFilter, 
                    UsernamePasswordAuthenticationFilter.class);
        
        System.out.println("✅ [SecurityConfig] SecurityFilterChain built successfully");
        System.out.println("✅ [SecurityConfig] JWT Filter added BEFORE UsernamePasswordAuthenticationFilter");
        System.out.println("🔧 [SecurityConfig] ========================================");
        return http.build();
    }
}
