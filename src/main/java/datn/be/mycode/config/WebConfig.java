package datn.be.mycode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebConfig {
    
    // CORS configuration is handled by WebSecurityConfigurerAdapter.corsConfigurationSource()
    // No need to configure CORS here to avoid conflicts with Spring Security CORS filter
    // Đọc CORS allowed origins từ environment variable hoặc application.properties
    // Format: "http://localhost:4200,https://yourdomain.com"
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;
    
    @Bean
    public WebMvcConfigurer WebMvcConfigurer(){
        return new WebMvcConfigurer(){
            // CORS is configured in WebSecurityConfigurerAdapter
            @Override
            public void addCorsMappings(CorsRegistry registry){
                // Split comma-separated origins
                String[] origins = allowedOrigins.split(",");
                for (int i = 0; i < origins.length; i++) {
                    origins[i] = origins[i].trim();
                }
                
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods(
                            HttpMethod.GET.name(),
                            HttpMethod.POST.name(),
                            HttpMethod.PUT.name(),
                            HttpMethod.DELETE.name(),
                            HttpMethod.OPTIONS.name(),
                            HttpMethod.PATCH.name()
                        )
                        .allowedHeaders(
                            HttpHeaders.CONTENT_TYPE,
                            HttpHeaders.AUTHORIZATION,
                            HttpHeaders.ACCEPT,
                            HttpHeaders.ORIGIN
                        )
                        .allowCredentials(true)
                        .maxAge(3600); // Cache preflight for 1 hour
            }
        };
    }
}
