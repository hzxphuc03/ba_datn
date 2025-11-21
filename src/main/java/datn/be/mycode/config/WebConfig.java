package datn.be.mycode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebConfig {
    
    // CORS configuration is handled by WebSecurityConfigurerAdapter.corsConfigurationSource()
    // No need to configure CORS here to avoid conflicts with Spring Security CORS filter
    
    @Bean
    public WebMvcConfigurer WebMvcConfigurer(){
        return new WebMvcConfigurer(){
            // CORS is configured in WebSecurityConfigurerAdapter
        };
    }
}
