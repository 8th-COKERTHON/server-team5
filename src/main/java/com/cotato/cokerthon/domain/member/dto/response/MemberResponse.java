package com.cotato.cokerthon.domain.member.dto.response;

import com.cotato.cokerthon.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보")
public record MemberResponse(
	@Schema(description = "회원 고유 ID", example = "1")
	Long memberId,

	@Schema(description = "로그인 아이디", example = "sleepair_user")
	String id,

	@Schema(description = "닉네임", example = "채은")
	String nickname
) {

	public static MemberResponse from(Member member) {
		return new MemberResponse(
			member.getId(),
			member.getLoginId(),
			member.getNickname()
		);
	}
}
