package br.com.fiap3esa.movesmart.infra.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String ESQUEMA_SEGURANCA = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API MoveSmart")
                        .version("1.0.0")
                        .description("API ReST do MoveSmart (Global Solution FIAP 2026.2 - CitizenTech): mobilidade urbana em São Paulo. "
                                + "Consulta linhas e previsões de chegada da SPTrans (Olho Vivo), recebe relatos de lotação dos passageiros e guarda as estimativas de atraso do modelo de Machine Learning. "
                                + "Faça login em POST /login e use o botão Authorize com o token JWT retornado."))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_SEGURANCA, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURANCA));
    }
}
