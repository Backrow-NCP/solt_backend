package org.backrow.solt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("solt-api")
                .packagesToScan("org.backrow.solt.controller")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("SOLT API 문서")
                        .version("0.0.1")
                        .description("SOLT 시스템에 대한 API 문서입니다."))
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
    }
}
