package com.cotato.cokerthon.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
	@Schema(description = "로그인 아이디 (소문자 영문, 숫자만 사용, 4~20자, 중복 불가)", example = "sleepair123")
	@NotBlank(message = "아이디는 필수입니다.")
	@Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
	@Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 소문자 영문과 숫자만 사용할 수 있습니다.")
	String id,

	@Schema(description = "비밀번호 (영문, 숫자, 특수문자를 모두 포함한 8~20자, 서버에는 BCrypt로 암호화되어 저장됨)", example = "password123!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]+$",
		message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다."
	)
	String password,

	@Schema(description = "닉네임 (한글, 영문, 숫자만 사용, 10자 이하)", example = "채은")
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.")
	String nickname
) {
}
