package com.cotato.cokerthon.domain.member.controller;

import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.domain.member.service.MemberService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 정보 조회 API")
@RestController
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@Operation(
		summary = "내 정보 조회",
		description = "Authorization 헤더에 담긴 Access Token으로 현재 로그인한 회원 본인의 정보를 조회합니다. "
			+ "가장 최근 수면시차 계산 결과를 기준으로 현재 머무는 도시와 위도/경도를 함께 내려주므로, "
			+ "지구본 화면에 내 위치를 찍을 때 사용할 수 있습니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (토큰 누락/만료/위조, AUTH_401)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "존재하지 않는 회원입니다 (COMMON_404)")
	})
	@GetMapping("/me")
	public ApiResponse<MemberResponse> getMyInfo(@AuthenticationPrincipal LoginMember loginMember) {
		return ApiResponse.ok(memberService.getMyInfo(loginMember.id()));
	}
}
