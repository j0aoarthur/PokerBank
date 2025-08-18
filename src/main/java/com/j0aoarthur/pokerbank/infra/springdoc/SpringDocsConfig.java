package com.j0aoarthur.pokerbank.infra.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
//                .components(new Components()
//                        .addSecuritySchemes("token-jwt",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("API do PokerBank")
                        .description("Esta é a API do back-end do sistema PokerBank, desenvolvido por João Arthur Britto.")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact().name("Desenvolvido por João Arthur Britto").url("https://joaoarthur.com")));
    }
}
