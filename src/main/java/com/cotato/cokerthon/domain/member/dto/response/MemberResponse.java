package com.cotato.cokerthon.domain.member.dto.response;

import com.cotato.cokerthon.domain.member.entity.Member;

public record MemberResponse(
	Long memberId,
	String id,
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
