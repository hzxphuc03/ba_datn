package datn.be.mycode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebConfig {
    
    // Đọc CORS allowed origins từ environment variable hoặc application.properties
    // Format: "http://localhost:4200,https://yourdomain.com"
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;
    
    @Bean
    public WebMvcConfigurer WebMvcConfigurer(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry){
                // Split comma-separated origins
                String[] origins = allowedOrigins.split(",");
                for (int i = 0; i < origins.length; i++) {
                    origins[i] = origins[i].trim();
                }
                
                // QUAN TRỌNG: Dùng allowedOriginPatterns() thay vì allowedOrigins()
                // Vì khi allowCredentials=true, không thể dùng allowedOrigins() với nhiều origins
                registry.addMapping("/**")
                        .allowedOriginPatterns(origins)  // Thay đổi từ allowedOrigins()
                        .allowedMethods(
                            HttpMethod.GET.name(),
                            HttpMethod.POST.name(),
                            HttpMethod.PUT.name(),
                            HttpMethod.DELETE.name(),
                            HttpMethod.OPTIONS.name(),
                            HttpMethod.PATCH.name()
                        )
                        .allowedHeaders("*")  // Cho phép tất cả headers
                        .allowCredentials(true)
                        .maxAge(3600); // Cache preflight for 1 hour
            }
        };
    }
}
