package com.cotato.cokerthon.domain.companion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "동행자 추가 요청")
public record CompanionCreateRequest(
	@Schema(description = "추가할 동행자의 로그인 아이디", example = "meangg")
	@NotBlank(message = "추가할 동행자의 아이디는 필수입니다.")
	String loginId
) {
}
