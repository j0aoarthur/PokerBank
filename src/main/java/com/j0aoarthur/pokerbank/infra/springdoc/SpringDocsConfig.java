package com.j0aoarthur.pokerbank.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Torna JWT obrigatório globalmente; endpoints públicos sobrescrevem com security = {}
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Insira o token JWT obtido no endpoint /auth/login")
                        ))
                .info(new Info()
                        .title("API do PokerBank")
                        .version("1.0.0")
                        .description("API back-end do sistema PokerBank. Gerencia clubes de poker, partidas, fichas, pagamentos e ranking de jogadores.")
                        .contact(new Contact()
                                .name("João Arthur Britto")
                                .url("https://joaoarthur.com")));
    }
}
