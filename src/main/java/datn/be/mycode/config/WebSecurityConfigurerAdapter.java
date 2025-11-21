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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

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
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        // Public endpoints - không cần authentication
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", 
                                       "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/auth/**", "/dang-ky/**", 
                                       "/quen-mat-khau/**").permitAll()
                        // Actuator health check endpoint - public for monitoring
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        
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
                .exceptionHandling(ex -> 
                    ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, 
                    UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
