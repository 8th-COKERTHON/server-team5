package com.cotato.cokerthon.domain.member.controller;

import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.domain.member.service.MemberService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@Operation(summary = "내 정보 조회", description = "Authorization 헤더의 Access Token으로 내 정보를 조회합니다.")
	@GetMapping("/me")
	public ApiResponse<MemberResponse> getMyInfo(@AuthenticationPrincipal LoginMember loginMember) {
		return ApiResponse.ok(memberService.getMyInfo(loginMember.id()));
	}
}
