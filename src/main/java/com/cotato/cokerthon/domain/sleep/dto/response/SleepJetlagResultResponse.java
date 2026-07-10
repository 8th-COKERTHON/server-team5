package com.cotato.cokerthon.domain.sleep.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.sleep.entity.JetlagDirection;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.entity.SleepRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "수면시차 계산 결과 (국제선 보딩패스 형태 - '오늘의 항공권')")
public record SleepJetlagResultResponse(
	@Schema(description = "계산 결과 고유 ID. 비회원 체험 계산은 저장되지 않아 null", example = "1", nullable = true)
	Long resultId,

	@Schema(description = "출발지 (항상 대한민국 서울)")
	CitySummaryResponse from,

	@Schema(description = "매칭된 도착지 (현재 수면 리듬과 시차가 같은 도시)")
	CitySummaryResponse to,

	@Schema(description = "현재(요즘) 수면 정보")
	SleepPeriodResponse currentSleep,

	@Schema(description = "목표(원하는) 수면 정보")
	SleepPeriodResponse targetSleep,

	@Schema(description = "수면시차 (분 단위, 최대 720)", example = "210")
	int jetlagMinutes,

	@Schema(description = "화면에 바로 표시할 수 있는 시차 라벨", example = "3시간 30분")
	String jetlagLabel,

	@Schema(
		description = "서울(목표) 대비 조정 방향. 프론트에서 '느려요/빨라요' 문구를 결정하는 데 사용. "
			+ "WEST=목표보다 늦게 자는 중(서울보다 느려요), EAST=목표보다 일찍 자는 중(서울보다 빨라요), "
			+ "SAME=시차가 거의 없음(30분 미만)",
		example = "WEST"
	)
	JetlagDirection direction,

	@Schema(description = "결과 조회일", example = "2026-07-11")
	LocalDate resultDate
) {

	public static SleepJetlagResultResponse from(SleepJetlagResult result, City fromCity) {
		SleepRecord sleepRecord = result.getSleepRecord();

		return new SleepJetlagResultResponse(
			result.getId(),
			CitySummaryResponse.from(fromCity),
			CitySummaryResponse.from(result.getMatchedCity()),
			new SleepPeriodResponse(
				sleepRecord.getCurrentBedtime(),
				sleepRecord.getCurrentWaketime(),
				sleepRecord.getCurrentSleepMinutes()
			),
			new SleepPeriodResponse(
				sleepRecord.getTargetBedtime(),
				sleepRecord.getTargetWaketime(),
				sleepRecord.getTargetSleepMinutes()
			),
			result.getJetlagMinutes(),
			formatJetlagLabel(result.getJetlagMinutes()),
			result.getDirection(),
			result.getResultDate()
		);
	}

	// 비회원 체험 계산 응답 생성 (SleepRecord/SleepJetlagResult를 저장하지 않으므로 엔티티 없이 조립)
	public static SleepJetlagResultResponse guest(
		LocalTime currentBedtime, LocalTime currentWaketime, int currentSleepMinutes,
		LocalTime targetBedtime, LocalTime targetWaketime, int targetSleepMinutes,
		int jetlagMinutes, JetlagDirection direction, City matchedCity, City fromCity
	) {
		return new SleepJetlagResultResponse(
			null,
			CitySummaryResponse.from(fromCity),
			CitySummaryResponse.from(matchedCity),
			new SleepPeriodResponse(currentBedtime, currentWaketime, currentSleepMinutes),
			new SleepPeriodResponse(targetBedtime, targetWaketime, targetSleepMinutes),
			jetlagMinutes,
			formatJetlagLabel(jetlagMinutes),
			direction,
			LocalDate.now()
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
