package com.cotato.cokerthon.domain.route.dto.response;

import com.cotato.cokerthon.domain.route.entity.ReturnRouteStatus;

public record ArrivalResponse(
	Long routeId,
	int nextDayNumber,
	ReturnRouteStatus status,
	CitySummaryResponse arrivedCity
) {
}
