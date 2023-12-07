package com.assignment.crypto.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Operation.class))
                .build();
    }

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info().title("Crypto Recommendation service API")
                        .description("API that recommends crypto based on metrics")
                        .version("1.0"));
    }
}
