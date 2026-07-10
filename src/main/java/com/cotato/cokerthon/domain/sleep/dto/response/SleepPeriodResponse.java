package com.cotato.cokerthon.domain.sleep.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "취침~기상 구간 정보 (현재 수면 또는 목표 수면)")
public record SleepPeriodResponse(
	@Schema(description = "취침 시각", example = "03:00")
	LocalTime bedtime,

	@Schema(description = "기상 시각", example = "10:00")
	LocalTime waketime,

	@Schema(description = "총 수면시간(분)", example = "420")
	int sleepMinutes
) {
}
