package com.cotato.cokerthon.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 시 발급되는 토큰 정보")
public record TokenResponse(
	@Schema(description = "토큰 타입", example = "Bearer")
	String grantType,

	@Schema(
		description = "Access Token. 이후 요청 시 Authorization 헤더에 'Bearer {accessToken}' 형태로 담아 보낸다.",
		example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.dGhpc19pc19hX2Zha2Vfand0X2V4YW1wbGU"
	)
	String accessToken
) {

	public static TokenResponse bearer(String accessToken) {
		return new TokenResponse("Bearer", accessToken);
	}
}
