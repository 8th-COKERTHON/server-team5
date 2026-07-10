package com.cotato.cokerthon.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 6, max = 30, message = "비밀번호는 6자 이상 30자 이하여야 합니다.")
	String password,

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
	String nickname
) {
}
