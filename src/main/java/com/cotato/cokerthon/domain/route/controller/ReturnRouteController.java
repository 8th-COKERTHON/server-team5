package com.cotato.cokerthon.domain.route.controller;

import com.cotato.cokerthon.domain.route.dto.response.ArrivalResponse;
import com.cotato.cokerthon.domain.route.dto.response.BoardingPassResponse;
import com.cotato.cokerthon.domain.route.dto.response.ReturnRouteResponse;
import com.cotato.cokerthon.domain.route.service.ReturnRouteService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/return-routes")
public class ReturnRouteController {

	private final ReturnRouteService returnRouteService;

	public ReturnRouteController(ReturnRouteService returnRouteService) {
		this.returnRouteService = returnRouteService;
	}

	@Operation(summary = "귀국 루트 생성", description = "수면시차 계산 결과를 기준으로 귀국 루트를 생성합니다.")
	@PostMapping("/results/{resultId}")
	public ApiResponse<ReturnRouteResponse> createReturnRoute(
		@AuthenticationPrincipal LoginMember loginMember,
		@PathVariable Long resultId
	) {
		return ApiResponse.ok(returnRouteService.createReturnRoute(loginMember.id(), resultId));
	}

	@Operation(summary = "현재 귀국 루트 조회", description = "진행 중인 귀국 루트와 오늘 이동할 경유지를 조회합니다.")
	@GetMapping("/current")
	public ApiResponse<ReturnRouteResponse> getCurrentRoute(@AuthenticationPrincipal LoginMember loginMember) {
		return ApiResponse.ok(returnRouteService.getCurrentRoute(loginMember.id()));
	}

	@Operation(summary = "귀국 항공권 조회", description = "현재 귀국 루트의 오늘 탑승권 정보를 조회합니다. 위치 상태는 변경하지 않습니다.")
	@GetMapping("/current/boarding-pass")
	public ApiResponse<BoardingPassResponse> getCurrentBoardingPass(
		@AuthenticationPrincipal LoginMember loginMember
	) {
		return ApiResponse.ok(returnRouteService.getCurrentBoardingPass(loginMember.id()));
	}

	@Operation(summary = "취침 후 다음 경유지 도착", description = "이제 잘게요 액션으로 현재 경유지 도착을 확정하고 다음 일차로 진행합니다.")
	@PostMapping("/current/sleep")
	public ApiResponse<ArrivalResponse> sleepAndArrive(@AuthenticationPrincipal LoginMember loginMember) {
		return ApiResponse.ok(returnRouteService.sleepAndArrive(loginMember.id()));
	}
}
