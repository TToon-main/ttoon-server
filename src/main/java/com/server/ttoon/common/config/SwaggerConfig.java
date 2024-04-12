package com.server.ttoon.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        String token = "Jwt Token";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(token);
        Components components = new Components().addSecuritySchemes(token, new SecurityScheme()
                .name(token)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("Jwt")
        );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {

        return new Info()
                .title("TToon Swagger UI")
                .description("TToon API 문서화")
                .version("1.0.0");
    }
}
