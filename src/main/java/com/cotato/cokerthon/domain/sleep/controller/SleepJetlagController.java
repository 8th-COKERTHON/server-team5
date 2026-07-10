package com.cotato.cokerthon.domain.sleep.controller;

import com.cotato.cokerthon.domain.sleep.dto.request.SleepJetlagRequest;
import com.cotato.cokerthon.domain.sleep.dto.response.SleepJetlagResultResponse;
import com.cotato.cokerthon.domain.sleep.service.SleepJetlagService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sleep Jetlag", description = "MVP1 - 나의 수면 국가 확인 (수면시차 계산) API")
@RestController
@RequestMapping("/api/sleep/jetlag")
public class SleepJetlagController {

	private final SleepJetlagService sleepJetlagService;

	public SleepJetlagController(SleepJetlagService sleepJetlagService) {
		this.sleepJetlagService = sleepJetlagService;
	}

	@Operation(
		summary = "수면시차 계산 (오늘의 항공권)",
		description = """
			현재 취침/기상 시간과 목표 취침/기상 시간을 입력받아 두 수면 리듬의 중간 시각 차이(수면시차)를 계산하고,
			서울과 그만큼 시차가 나는 도시를 매칭해 국제선 보딩패스 형태의 결과로 반환합니다.

			- 수면시차 = |현재 수면 중간시각 - 목표 수면 중간시각| (원형 거리 기준, 최대 12시간=720분)
			- 현재 수면이 목표보다 늦으면 WEST, 이르면 EAST, 30분 미만 차이면 SAME(서울)으로 매칭됩니다.
			- 같은 시차 구간에 여러 도시가 매핑되어 있으면 그중 하나가 랜덤으로 선택됩니다.
			- 호출할 때마다 새 계산 결과가 생성되어 이력으로 남으며(재측정), 이전 결과는 덮어써지지 않습니다.
			- 로그인한 회원만 호출할 수 있고, 결과는 호출한 회원 소유로 저장됩니다.
			"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "계산 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "요청 값 검증 실패 (취침/기상 시간 누락 COMMON_400, 시간 형식 오류 SLEEP_400_001)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (토큰 누락/만료/위조, AUTH_401)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "존재하지 않는 회원입니다 (COMMON_404)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500", description = "시차에 매칭되는 도시를 찾지 못함 (시드 데이터 누락 등 서버 설정 오류, SLEEP_500_001)")
	})
	@PostMapping
	public ApiResponse<SleepJetlagResultResponse> calculate(
		@AuthenticationPrincipal LoginMember loginMember,
		@Valid @RequestBody SleepJetlagRequest request
	) {
		return ApiResponse.ok(sleepJetlagService.calculate(loginMember.id(), request));
	}
}
