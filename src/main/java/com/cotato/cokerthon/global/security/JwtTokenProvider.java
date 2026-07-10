package com.cotato.cokerthon.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpirationMillis;

	public JwtTokenProvider(
		@Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.access-token-expiration-millis}") long accessTokenExpirationMillis
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpirationMillis = accessTokenExpirationMillis;
	}

	public String createAccessToken(Long memberId) {
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + accessTokenExpirationMillis);

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(now)
			.expiration(expiresAt)
			.signWith(secretKey)
			.compact();
	}

	public Long getMemberId(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		return Long.valueOf(claims.getSubject());
	}
}
