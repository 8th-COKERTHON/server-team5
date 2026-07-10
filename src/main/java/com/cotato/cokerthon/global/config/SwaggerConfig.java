package com.cotato.cokerthon.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	private final String serverUrl;

	public SwaggerConfig(@Value("${app.swagger.server-url:}") String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Bean
	public OpenAPI openAPI() {
		OpenAPI openAPI = new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new Components()
				.addSecuritySchemes("bearerAuth", new SecurityScheme()
					.name("bearerAuth")
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")))
			.info(new Info()
				.title("8th Cokerthon API")
				.description("8th Cokerthon backend API documentation")
				.version("v1"));

		if (!serverUrl.isBlank()) {
			openAPI.servers(List.of(new Server().url(validateServerUrl(serverUrl))));
		}

		return openAPI;
	}

	private String validateServerUrl(String serverUrl) {
		URI uri = URI.create(serverUrl);
		String scheme = uri.getScheme();
		if (!"http".equals(scheme) && !"https".equals(scheme)) {
			throw new IllegalArgumentException("app.swagger.server-url must start with http:// or https://");
		}
		return serverUrl;
	}
}
