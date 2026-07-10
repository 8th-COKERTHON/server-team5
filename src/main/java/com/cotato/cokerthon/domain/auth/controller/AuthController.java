package com.cotato.cokerthon.domain.auth.controller;

import com.cotato.cokerthon.domain.auth.dto.request.LoginRequest;
import com.cotato.cokerthon.domain.auth.dto.request.SignupRequest;
import com.cotato.cokerthon.domain.auth.dto.response.TokenResponse;
import com.cotato.cokerthon.domain.auth.service.AuthService;
import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "회원가입", description = "아이디, 비밀번호, 닉네임으로 회원가입합니다.")
	@PostMapping("/signup")
	public ApiResponse<MemberResponse> signup(@Valid @RequestBody SignupRequest request) {
		return ApiResponse.ok(authService.signup(request));
	}

	@Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 Access Token을 발급받습니다.")
	@PostMapping("/login")
	public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.ok(authService.login(request));
	}
}
