package com.cotato.cokerthon.domain.route.dto.response;

import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import java.time.LocalTime;

public record ReturnRouteDayResponse(
	int dayNumber,
	CitySummaryResponse checkpointCity,
	LocalTime currentBedtime,
	LocalTime currentWaketime,
	LocalTime targetBedtime,
	LocalTime targetWaketime,
	LocalTime caffeineCutoffTime,
	LocalTime sleepPrepTime,
	LocalTime bedtimeWindowStart,
	LocalTime bedtimeWindowEnd
) {

	public static ReturnRouteDayResponse from(ReturnRouteDay day) {
		return new ReturnRouteDayResponse(
			day.getDayNumber(),
			CitySummaryResponse.from(day.getCheckpointCity()),
			day.getCurrentBedtime(),
			day.getCurrentWaketime(),
			day.getTargetBedtime(),
			day.getTargetWaketime(),
			day.getCaffeineCutoffTime(),
			day.getSleepPrepTime(),
			day.getBedtimeWindowStart(),
			day.getBedtimeWindowEnd()
		);
	}
}
