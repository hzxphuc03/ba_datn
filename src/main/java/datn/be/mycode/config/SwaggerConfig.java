package datn.be.mycode.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
public class SwaggerConfig {
    
    @Value("${app.swagger.enabled:false}")
    private boolean swaggerEnabled;
    
    @Bean
    @Profile("!prod") // Chỉ enable Swagger khi không phải production
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DATN API")
                        .version("1.0.0")
                        .description("API documentation - Chỉ dành cho môi trường phát triển"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token để xác thực")));
    }
}
