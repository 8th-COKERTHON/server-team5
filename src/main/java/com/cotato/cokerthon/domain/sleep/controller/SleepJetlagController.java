package com.cotato.cokerthon.domain.sleep.controller;

import com.cotato.cokerthon.domain.sleep.dto.request.SleepJetlagRequest;
import com.cotato.cokerthon.domain.sleep.dto.response.SleepJetlagResultResponse;
import com.cotato.cokerthon.domain.sleep.service.SleepJetlagService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

			**로그인 회원**: Authorization 헤더로 호출하면 매번 새 계산 결과가 이력으로 저장되며 횟수 제한이 없습니다.

			**비회원(게스트)**: Authorization 헤더 없이 `X-Device-Id` 헤더(클라이언트가 생성해 보관하는 기기 식별자)만
			담아 호출하면 딱 1회 계산할 수 있습니다. 계산 결과는 서버에 저장되지 않고(`resultId`=null), 같은
			`X-Device-Id`로 다시 호출하면 403(GUEST_TRIAL_EXHAUSTED)이 나며 이때부터는 회원가입 후 로그인해야
			다시 이용할 수 있습니다.
			"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "계산 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 검증 실패 (취침/기상 시간 누락 COMMON_400, 시간 형식 오류 SLEEP_400_001), "
				+ "또는 비회원인데 X-Device-Id 헤더 누락 (SLEEP_400_002)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403", description = "비회원 체험을 이미 사용함, 회원가입 필요 (SLEEP_403_001)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "존재하지 않는 회원입니다 (COMMON_404)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500", description = "시차에 매칭되는 도시를 찾지 못함 (시드 데이터 누락 등 서버 설정 오류, SLEEP_500_001)")
	})
	@PostMapping
	public ApiResponse<SleepJetlagResultResponse> calculate(
		@AuthenticationPrincipal LoginMember loginMember,
		@Parameter(description = "비회원 호출 시에만 필요한 기기 식별자 (클라이언트 생성 UUID 등)")
		@RequestHeader(value = "X-Device-Id", required = false) String deviceId,
		@Valid @RequestBody SleepJetlagRequest request
	) {
		Long memberId = loginMember != null ? loginMember.id() : null;
		return ApiResponse.ok(sleepJetlagService.calculate(memberId, deviceId, request));
	}
}
