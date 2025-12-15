package datn.be.mycode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class WebConfig {
    
    // NOTE: CORS được handle bởi Spring Security (WebSecurityConfigurerAdapter)
    // Không cần CORS config ở đây để tránh conflict với Spring Security CORS
}
