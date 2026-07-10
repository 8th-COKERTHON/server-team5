package com.cotato.cokerthon.domain.route.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;

public record CitySummaryResponse(
	Integer id,
	String countryName,
	String cityNameKr,
	String cityNameEn,
	String airportCode,
	String utcOffset,
	String flagImageUrl
) {

	public static CitySummaryResponse from(City city) {
		return new CitySummaryResponse(
			city.getId(),
			city.getCountryName(),
			city.getCityNameKr(),
			city.getCityNameEn(),
			city.getAirportCode(),
			city.getUtcOffset(),
			city.getFlagImageUrl()
		);
	}
}
