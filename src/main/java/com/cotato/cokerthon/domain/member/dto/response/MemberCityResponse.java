package com.cotato.cokerthon.domain.member.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내가 현재 머무는 수면 도시 정보 (지구본에 내 위치를 찍을 때 사용)")
public record MemberCityResponse(
	@Schema(description = "국가명", example = "인도")
	String countryName,

	@Schema(description = "도시명 (한글)", example = "뉴델리")
	String cityNameKr,

	@Schema(description = "영문 도시명", example = "NEW DELHI")
	String cityNameEn,

	@Schema(description = "IATA 공항 코드", example = "DEL")
	String airportCode,

	@Schema(description = "위도 (프론트 지구본 매핑용)", example = "28.6139")
	double latitude,

	@Schema(description = "경도 (프론트 지구본 매핑용)", example = "77.2090")
	double longitude
) {

	public static MemberCityResponse from(City city) {
		return new MemberCityResponse(
			city.getCountryName(),
			city.getCityNameKr(),
			city.getCityNameEn(),
			city.getAirportCode(),
			city.getLatitude(),
			city.getLongitude()
		);
	}
}
