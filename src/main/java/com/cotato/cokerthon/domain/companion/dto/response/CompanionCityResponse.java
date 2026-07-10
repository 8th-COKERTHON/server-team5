package com.cotato.cokerthon.domain.companion.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행자 카드에 표시되는 현재 수면 도시 정보")
public record CompanionCityResponse(
	@Schema(description = "국가명", example = "태국")
	String countryName,

	@Schema(description = "도시명 (한글)", example = "방콕")
	String cityNameKr,

	@Schema(description = "영문 도시명", example = "BANGKOK")
	String cityNameEn,

	@Schema(description = "IATA 공항 코드", example = "BKK")
	String airportCode,

	@Schema(description = "위도 (프론트 지구본 매핑용)", example = "13.7563")
	double latitude,

	@Schema(description = "경도 (프론트 지구본 매핑용)", example = "100.5018")
	double longitude
) {

	public static CompanionCityResponse from(City city) {
		return new CompanionCityResponse(
			city.getCountryName(),
			city.getCityNameKr(),
			city.getCityNameEn(),
			city.getAirportCode(),
			city.getLatitude(),
			city.getLongitude()
		);
	}
}
