package com.cotato.cokerthon.domain.route.dto.response;

import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStatus;
import java.util.List;

public record ReturnRouteResponse(
	Long routeId,
	int dailyAdjustMinutes,
	int durationDays,
	int currentDayNumber,
	ReturnRouteStatus status,
	CitySummaryResponse departureCity,
	CitySummaryResponse currentArrivalCity,
	List<ReturnRouteDayResponse> days
) {

	public static ReturnRouteResponse of(
		ReturnRoute route,
		CitySummaryResponse departureCity,
		ReturnRouteDay currentDay,
		List<ReturnRouteDay> days
	) {
		return new ReturnRouteResponse(
			route.getId(),
			route.getDailyAdjustMinutes(),
			route.getDurationDays(),
			route.getCurrentDayNumber(),
			route.getStatus(),
			departureCity,
			currentDay == null ? null : CitySummaryResponse.from(currentDay.getCheckpointCity()),
			days.stream().map(ReturnRouteDayResponse::from).toList()
		);
	}
}
