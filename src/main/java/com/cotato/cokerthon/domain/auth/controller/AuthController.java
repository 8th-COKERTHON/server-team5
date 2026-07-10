package com.cotato.cokerthon.domain.auth.controller;

import com.cotato.cokerthon.domain.auth.dto.request.LoginRequest;
import com.cotato.cokerthon.domain.auth.dto.request.SignupRequest;
import com.cotato.cokerthon.domain.auth.dto.response.TokenResponse;
import com.cotato.cokerthon.domain.auth.service.AuthService;
import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입 · 로그인 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(
		summary = "회원가입",
		description = """
			아이디, 비밀번호, 닉네임으로 회원가입합니다.

			- 아이디는 영문/숫자/밑줄 4~20자이며 중복될 수 없습니다.
			- 비밀번호는 6~30자이며 BCrypt로 암호화되어 저장됩니다.
			- 가입 직후 자동 로그인되지 않으므로, 이어서 로그인 API를 호출해 Access Token을 발급받아야 합니다.
			"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "요청 값 검증 실패 (아이디/비밀번호/닉네임 형식 오류, COMMON_400)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409", description = "이미 사용 중인 아이디 (MEMBER_409_001)")
	})
	@PostMapping("/signup")
	public ApiResponse<MemberResponse> signup(@Valid @RequestBody SignupRequest request) {
		return ApiResponse.ok(authService.signup(request));
	}

	@Operation(
		summary = "로그인",
		description = """
			아이디와 비밀번호로 로그인하고 Access Token을 발급받습니다.
			응답의 data.accessToken 값을 이후 요청의 Authorization 헤더에 'Bearer {accessToken}' 형태로 담아 보내야 인증이 필요한 API를 호출할 수 있습니다.
			"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "로그인 성공, Access Token 발급"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "아이디 또는 비밀번호가 올바르지 않음 (AUTH_401_001)")
	})
	@PostMapping("/login")
	public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.ok(authService.login(request));
	}
}
