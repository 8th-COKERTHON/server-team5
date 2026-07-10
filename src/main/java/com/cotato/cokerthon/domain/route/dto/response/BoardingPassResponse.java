package com.cotato.cokerthon.domain.route.dto.response;

import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import java.time.LocalDate;
import java.time.LocalTime;

public record BoardingPassResponse(
	Long routeId,
	int dayNumber,
	LocalDate boardingDate,
	CitySummaryResponse departureCity,
	CitySummaryResponse arrivalCity,
	LocalTime currentBedtime,
	LocalTime currentWaketime,
	LocalTime targetBedtime,
	LocalTime targetWaketime,
	LocalTime caffeineCutoffTime,
	LocalTime sleepPrepTime,
	LocalTime bedtimeWindowStart,
	LocalTime bedtimeWindowEnd
) {

	public static BoardingPassResponse of(Long routeId, LocalDate boardingDate,
		CitySummaryResponse departureCity, ReturnRouteDay routeDay) {
		return new BoardingPassResponse(
			routeId,
			routeDay.getDayNumber(),
			boardingDate,
			departureCity,
			CitySummaryResponse.from(routeDay.getCheckpointCity()),
			routeDay.getCurrentBedtime(),
			routeDay.getCurrentWaketime(),
			routeDay.getTargetBedtime(),
			routeDay.getTargetWaketime(),
			routeDay.getCaffeineCutoffTime(),
			routeDay.getSleepPrepTime(),
			routeDay.getBedtimeWindowStart(),
			routeDay.getBedtimeWindowEnd()
		);
	}
}
