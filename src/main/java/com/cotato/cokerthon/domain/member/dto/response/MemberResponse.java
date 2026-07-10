package com.cotato.cokerthon.domain.member.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.sleep.entity.JetlagDirection;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "회원 정보")
public record MemberResponse(
	@Schema(description = "회원 고유 ID", example = "1")
	Long memberId,

	@Schema(description = "로그인 아이디", example = "sleepair_user")
	String id,

	@Schema(description = "닉네임", example = "채은")
	String nickname,

	@Schema(description = "현재 머무는 수면 도시. 아직 여행을 떠난 적 없으면 서울")
	MemberCityResponse city,

	@Schema(description = "수면시차(분). 아직 여행을 떠난 적 없으면 0", example = "210")
	int jetlagMinutes,

	@Schema(description = "화면에 바로 표시할 수 있는 시차 라벨. 아직 여행을 떠난 적 없으면 \"0분\"", example = "3시간 30분")
	String jetlagLabel,

	@Schema(description = "서울 대비 조정 방향. 아직 여행을 떠난 적 없으면 SAME", example = "WEST")
	JetlagDirection direction,

	@Schema(description = "마지막 기록 시각. 실제로 계산한 기록이 없으면 null", nullable = true)
	LocalDateTime lastRecordedAt
) {

	public static MemberResponse from(Member member, SleepJetlagResult latestResult, City seoul) {
		if (latestResult == null) {
			return new MemberResponse(
				member.getId(),
				member.getLoginId(),
				member.getNickname(),
				MemberCityResponse.from(seoul),
				0,
				"0분",
				JetlagDirection.SAME,
				null
			);
		}

		return new MemberResponse(
			member.getId(),
			member.getLoginId(),
			member.getNickname(),
			MemberCityResponse.from(latestResult.getMatchedCity()),
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
