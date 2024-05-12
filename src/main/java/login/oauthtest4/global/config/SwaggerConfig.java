package login.oauthtest4.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
        info = @Info(
                title = "티키챗 서버 회원/인증 API",
                description = "티키챗 서버 회원/인증 모듈 API 명세서",
                version = "v1"
        )
)
@Configuration
@Profile(value = "!prod")
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";
    private static final String SECURITY_JWT_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_JWT_NAME);
        Components components = new Components()
                .addSecuritySchemes(SECURITY_JWT_NAME, new SecurityScheme()
                        .name(SECURITY_JWT_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat(SECURITY_JWT_NAME));

        return new OpenAPI()
                .components(components);
    }
}
