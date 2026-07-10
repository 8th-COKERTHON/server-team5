package com.cotato.cokerthon.domain.auth.dto.response;

public record TokenResponse(
	String grantType,
	String accessToken
) {

	public static TokenResponse bearer(String accessToken) {
		return new TokenResponse("Bearer", accessToken);
	}
}
