package com.cotato.cokerthon.domain.companion.controller;

import com.cotato.cokerthon.domain.companion.dto.request.CompanionCreateRequest;
import com.cotato.cokerthon.domain.companion.dto.response.CompanionResponse;
import com.cotato.cokerthon.domain.companion.dto.response.CompanionSearchResponse;
import com.cotato.cokerthon.domain.companion.service.CompanionService;
import com.cotato.cokerthon.global.response.ApiResponse;
import com.cotato.cokerthon.global.security.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Companion", description = "MVP2 - 친구와 수면 국가 공유 (동행자) API")
@RestController
@RequestMapping("/api/companions")
public class CompanionController {

	private final CompanionService companionService;

	public CompanionController(CompanionService companionService) {
		this.companionService = companionService;
	}

	@Operation(
		summary = "동행자 찾기 검색",
		description = "로그인 아이디로 동행자가 될 상대를 검색합니다. 이미 동행자로 추가된 상대인지 여부도 함께 내려줍니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (AUTH_401)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "해당 아이디의 회원이 존재하지 않습니다 (COMMON_404)")
	})
	@GetMapping("/search")
	public ApiResponse<CompanionSearchResponse> search(
		@AuthenticationPrincipal LoginMember loginMember,
		@RequestParam String loginId
	) {
		return ApiResponse.ok(companionService.search(loginMember.id(), loginId));
	}

	@Operation(
		summary = "동행자 추가",
		description = """
			입력한 아이디의 회원을 동행자로 추가합니다.

			- 상호 동의 절차 없는 단방향 관계입니다. 추가 버튼을 누르는 즉시 등록됩니다 (상대方 수락 불필요).
			- 상대방의 동행자 목록에는 나타나지 않으며, 나의 목록에만 표시됩니다.
			- 자기 자신은 추가할 수 없고, 이미 추가한 상대를 다시 추가할 수 없습니다.
			"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "자기 자신을 추가하려 함 (COMPANION_400_001)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (AUTH_401)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "해당 아이디의 회원이 존재하지 않습니다 (COMMON_404)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409", description = "이미 추가한 동행자입니다 (COMPANION_409_001)")
	})
	@PostMapping
	public ApiResponse<CompanionResponse> add(
		@AuthenticationPrincipal LoginMember loginMember,
		@Valid @RequestBody CompanionCreateRequest request
	) {
		return ApiResponse.ok(companionService.add(loginMember.id(), request));
	}

	@Operation(
		summary = "동행자 목록 조회",
		description = "내가 추가한 동행자들의 닉네임, 현재 수면 도시, 서울과의 수면시차, 마지막 기록 시각을 조회합니다. "
			+ "아직 수면시차를 계산한 적 없는 동행자는 서울, 0분, SAME으로 내려갑니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (AUTH_401)")
	})
	@GetMapping
	public ApiResponse<List<CompanionResponse>> getCompanions(@AuthenticationPrincipal LoginMember loginMember) {
		return ApiResponse.ok(companionService.getCompanions(loginMember.id()));
	}

	@Operation(
		summary = "동행자 삭제",
		description = "동행자 목록에서 특정 동행자와의 연결을 해제합니다. 단방향 연결만 삭제되며 상대방에게는 영향이 없습니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증이 필요합니다 (AUTH_401)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "동행자로 추가되어 있지 않음 (COMMON_404)")
	})
	@DeleteMapping("/{companionMemberId}")
	public ApiResponse<Void> remove(
		@AuthenticationPrincipal LoginMember loginMember,
		@PathVariable Long companionMemberId
	) {
		companionService.remove(loginMember.id(), companionMemberId);
		return ApiResponse.ok();
	}
}
