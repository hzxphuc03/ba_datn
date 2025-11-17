package datn.be.mycode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:3000}")
    private String allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;
    
    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cấu hình origins được phép
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOriginPatterns(origins);
        
        // Cấu hình methods được phép
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);
        
        // Cấu hình headers được phép
        if ("*".equals(allowedHeaders)) {
            configuration.setAllowedHeaders(Arrays.asList("*"));
        } else {
            List<String> headers = Arrays.asList(allowedHeaders.split(","));
            configuration.setAllowedHeaders(headers);
        }
        
        // Cho phép credentials (cookies, authorization headers)
        configuration.setAllowCredentials(allowCredentials);
        
        // Expose headers cho client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}