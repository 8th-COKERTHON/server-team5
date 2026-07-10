package com.cotato.cokerthon.domain.companion.dto.response;

import com.cotato.cokerthon.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행자 찾기 검색 결과")
public record CompanionSearchResponse(
	@Schema(description = "검색된 회원 ID", example = "3")
	Long memberId,

	@Schema(description = "검색된 회원의 로그인 아이디", example = "meangg")
	String loginId,

	@Schema(description = "닉네임", example = "민주")
	String nickname,

	@Schema(description = "프로필 이미지 URL", example = "https://cdn.sleepair.app/profile/3.png", nullable = true)
	String profileImageUrl,

	@Schema(description = "이미 내 동행자 목록에 추가된 상대인지 여부", example = "false")
	boolean alreadyCompanion
) {

	public static CompanionSearchResponse of(Member member, boolean alreadyCompanion) {
		return new CompanionSearchResponse(
			member.getId(),
			member.getLoginId(),
			member.getNickname(),
			member.getProfileImageUrl(),
			alreadyCompanion
		);
	}
}
