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
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import datn.be.mycode.config.JwtTokenProvider;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    public WebSecurityConfigurerAdapter(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                       JwtTokenProvider jwtTokenProvider) {
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * SecurityFilterChain cho /api/auth/** - Order 0 để được xử lý trước
     * KHÔNG có JWT filter và KHÔNG có exception handling
     */
    @Bean
    @Order(0)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"WebSecurityConfigurerAdapter.java:40\",\"message\":\"authSecurityFilterChain bean created\",\"data\":{\"order\":0,\"matcher\":\"lambda\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        http
                // Sử dụng securityMatcher để chỉ áp dụng chain này cho /api/auth/** và /error
                .securityMatcher("/api/auth/**", "/error")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // #region agent log
                    try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"WebSecurityConfigurerAdapter.java:58\",\"message\":\"authSecurityFilterChain authorizeHttpRequests permitAll configured\",\"data\":{},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                    // #endregion
                    auth.anyRequest().permitAll();
                });
        
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"WebSecurityConfigurerAdapter.java:66\",\"message\":\"authSecurityFilterChain built - no filter, exception handling disabled\",\"data\":{},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        
        return http.build();
    }

    /**
     * SecurityFilterChain cho các endpoints khác - Order 1
     * Có JWT filter và exception handling
     * KHÔNG match với /api/auth/** vì đã được xử lý bởi authSecurityFilterChain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B\",\"location\":\"WebSecurityConfigurerAdapter.java:58\",\"message\":\"securityFilterChain bean created\",\"data\":{\"order\":1},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
        // #endregion
        http
                // Đảm bảo chain này KHÔNG match với /api/auth/**
                .securityMatcher(request -> {
                    String path = request.getServletPath();
                    // #region agent log
                    try { java.nio.file.Files.write(java.nio.file.Paths.get("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log"), (java.time.Instant.now().toEpochMilli() + "|{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"E\",\"location\":\"WebSecurityConfigurerAdapter.java:63\",\"message\":\"securityMatcher lambda evaluated\",\"data\":{\"path\":\"" + path + "\",\"matches\":\"" + !path.startsWith("/api/auth/") + "\"},\"timestamp\":" + java.time.Instant.now().toEpochMilli() + "}\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch (Exception e) {}
                    // #endregion
                    return !path.startsWith("/api/auth/");
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        // QUAN TRỌNG: Cho phép OPTIONS request (preflight CORS) - PHẢI ĐẶT ĐẦU TIÊN
                        .requestMatchers("OPTIONS", "/**").permitAll()
                        
                        // Public endpoints - PHẢI ĐẶT TRƯỚC TẤT CẢ
                        // Lưu ý: /api/auth/** đã được xử lý bởi authSecurityFilterChain
                        .requestMatchers("/dang-ky/**").permitAll()
                        .requestMatchers("/quen-mat-khau/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        
                        // WebSocket endpoints - public
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        
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
                        .anyRequest().authenticated()
                )
                // JWT filter sẽ tự bỏ qua public endpoints qua shouldNotFilter()
                // Phải thêm filter TRƯỚC exception handling để filter có thể xử lý trước
                .addFilterBefore(jwtAuthenticationFilter, 
                    UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> 
                    ex.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        
        return http.build();
    }
    
    /**
     * CORS Configuration Source - Đọc từ environment variable CORS_ALLOWED_ORIGINS
     * Sử dụng allowedOriginPatterns() thay vì allowedOrigins() để tương thích với allowCredentials(true)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Đọc từ @Value (sẽ đọc từ app.cors.allowed-origins trong application.properties)
        // application-prod.properties đọc từ CORS_ALLOWED_ORIGINS env var
        String origins = allowedOrigins;
        
        // Fallback: Đọc trực tiếp từ environment variable nếu @Value rỗng
        if (origins == null || origins.isEmpty()) {
            origins = System.getenv("CORS_ALLOWED_ORIGINS");
            if (origins == null || origins.isEmpty()) {
                origins = "http://localhost:4200";
                System.out.println("WARNING: CORS_ALLOWED_ORIGINS not set, using default: " + origins);
            }
        }
        
        // Split và trim origins
        String[] originsArray = origins.split(",");
        for (int i = 0; i < originsArray.length; i++) {
            originsArray[i] = originsArray[i].trim();
        }
        
        // Log để debug
        System.out.println("=== CORS Configuration ===");
        System.out.println("Raw allowedOrigins from @Value: " + allowedOrigins);
        System.out.println("Final allowedOrigins: " + origins);
        System.out.println("Allowed Origins Array: " + Arrays.toString(originsArray));
        System.out.println("===========================");
        
        // QUAN TRỌNG: Dùng setAllowedOriginPatterns() thay vì setAllowedOrigins()
        // Vì khi allowCredentials=true, không thể dùng setAllowedOrigins() với nhiều origins
        // allowedOriginPatterns() cho phép dùng wildcard và credentials cùng lúc
        configuration.setAllowedOriginPatterns(Arrays.asList(originsArray));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // Expose headers để frontend có thể đọc được
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
