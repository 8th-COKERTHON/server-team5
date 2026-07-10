package com.cotato.cokerthon.domain.sleep.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Schema(description = "수면시차 계산 요청 (현재/목표 취침·기상 시간)")
public record SleepJetlagRequest(
	@Schema(description = "요즘 실제로 잠드는 시간", example = "03:00")
	@NotNull(message = "현재 잠드는 시간은 필수입니다.")
	LocalTime currentBedtime,

	@Schema(description = "요즘 실제로 일어나는 시간", example = "10:00")
	@NotNull(message = "현재 기상 시간은 필수입니다.")
	LocalTime currentWaketime,

	@Schema(description = "목표(원하는) 잠드는 시간", example = "23:00")
	@NotNull(message = "목표 잠드는 시간은 필수입니다.")
	LocalTime targetBedtime,

	@Schema(description = "목표(원하는) 기상 시간", example = "07:00")
	@NotNull(message = "목표 기상 시간은 필수입니다.")
	LocalTime targetWaketime
) {
}
