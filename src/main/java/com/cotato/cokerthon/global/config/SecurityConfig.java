package com.cotato.cokerthon.global.config;

import com.cotato.cokerthon.global.security.JwtAuthenticationEntryPoint;
import com.cotato.cokerthon.global.security.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

	private static final String[] PUBLIC_PATHS = {
		"/api/auth/**",
		"/actuator/health",
		"/actuator/health/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/v3/api-docs/**"
	};

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final List<String> allowedOrigins;
	private final List<String> extraAllowedOrigins;

	public SecurityConfig(
		JwtAuthenticationFilter jwtAuthenticationFilter,
		JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
		@Value("${cors.allowed-origins:}") String allowedOrigins,
		@Value("${cors.extra-allowed-origins:}") String extraAllowedOrigins
	) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.allowedOrigins = parseOrigins(allowedOrigins);
		this.extraAllowedOrigins = parseOrigins(extraAllowedOrigins);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PUBLIC_PATHS).permitAll()
				// 비회원도 1회 체험 계산이 가능해야 해서 인증을 강제하지 않고, 회원/비회원 분기는 서비스 로직에서 처리
				.requestMatchers(HttpMethod.POST, "/api/sleep/jetlag").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Stream.concat(allowedOrigins.stream(), extraAllowedOrigins.stream())
			.distinct()
			.toList());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private List<String> parseOrigins(String origins) {
		return Arrays.stream(origins.split(","))
			.map(String::trim)
			.filter(origin -> !origin.isBlank())
			.toList();
	}
}
