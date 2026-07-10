package com.cotato.cokerthon.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
	@Schema(description = "로그인 아이디 (영문, 숫자, 밑줄만 사용, 4~20자, 중복 불가)", example = "sleepair_user")
	@NotBlank(message = "아이디는 필수입니다.")
	@Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 밑줄만 사용할 수 있습니다.")
	String id,

	@Schema(description = "비밀번호 (6~30자, 서버에는 BCrypt로 암호화되어 저장됨)", example = "password123!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 6, max = 30, message = "비밀번호는 6자 이상 30자 이하여야 합니다.")
	String password,

	@Schema(description = "닉네임 (동행자 카드 등에 노출, 30자 이하)", example = "채은")
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
	String nickname
) {
}
