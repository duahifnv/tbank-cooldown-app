package org.svids.tbankcooldownapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OpenAPIConfig {

    @Bean
    @Profile("local")
    public OpenAPI localOpenAPI(@Value("${server.port}") int port) {
        return new OpenAPI()
                .info(new Info().title("API сервиса рационального ассистента (локально)"))
                .addServersItem(new Server()
                        .url("http://localhost:" + port)
                        .description("Local development"));
    }

    @Bean
    @Profile("prod")
    public OpenAPI tunnelOpenAPI(@Value("${tunnel.url}") String tunnelUrl) {
        return new OpenAPI()
                .info(new Info().title("API сервиса рационального ассистента (продакшн)"))
                .addServersItem(new Server()
                        .url(tunnelUrl)
                        .description("Tunnel development"));
    }
}
