package com.cotato.cokerthon.domain.companion.dto.response;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.sleep.entity.JetlagDirection;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "동행자 카드 (같이 여행 중인 친구)")
public record CompanionResponse(
	@Schema(description = "동행자의 회원 ID (삭제 시 사용)", example = "2")
	Long companionMemberId,

	@Schema(description = "동행자 닉네임", example = "민지")
	String nickname,

	@Schema(description = "동행자 프로필 이미지 URL", example = "https://cdn.sleepair.app/profile/2.png", nullable = true)
	String profileImageUrl,

	@Schema(description = "동행자의 현재 수면 도시. 아직 계산 기록이 없으면 null", nullable = true)
	CompanionCityResponse city,

	@Schema(description = "동행자의 수면시차(분). 기록이 없으면 null", example = "120", nullable = true)
	Integer jetlagMinutes,

	@Schema(description = "화면에 바로 표시할 수 있는 시차 라벨. 기록이 없으면 null", example = "2시간", nullable = true)
	String jetlagLabel,

	@Schema(description = "서울 대비 조정 방향. 기록이 없으면 null", example = "WEST", nullable = true)
	JetlagDirection direction,

	@Schema(description = "마지막 기록 시각. 기록이 없으면 null", nullable = true)
	LocalDateTime lastRecordedAt
) {

	public static CompanionResponse of(Member companionMember, SleepJetlagResult latestResult) {
		if (latestResult == null) {
			return new CompanionResponse(
				companionMember.getId(),
				companionMember.getNickname(),
				companionMember.getProfileImageUrl(),
				null, null, null, null, null
			);
		}

		return new CompanionResponse(
			companionMember.getId(),
			companionMember.getNickname(),
			companionMember.getProfileImageUrl(),
			CompanionCityResponse.from(latestResult.getMatchedCity()),
			latestResult.getJetlagMinutes(),
			formatJetlagLabel(latestResult.getJetlagMinutes()),
			latestResult.getDirection(),
			latestResult.getCreatedAt()
		);
	}

	private static String formatJetlagLabel(int jetlagMinutes) {
		int hours = jetlagMinutes / 60;
		int minutes = jetlagMinutes % 60;

		if (hours == 0) {
			return minutes + "분";
		}
		if (minutes == 0) {
			return hours + "시간";
		}
		return hours + "시간 " + minutes + "분";
	}
}
