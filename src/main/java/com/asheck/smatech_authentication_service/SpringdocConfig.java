package com.asheck.smatech_authentication_service;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
@SecurityScheme(
        type = SecuritySchemeType.APIKEY, name = "authorization", in = SecuritySchemeIn.HEADER)
public class SpringdocConfig {

    @Bean
    public OpenAPI baseOpenApi(){
        return new OpenAPI().info(new Info().title("Smatech Auth").version("0.8.5").description("Smatech Auth API"));
    }
}
