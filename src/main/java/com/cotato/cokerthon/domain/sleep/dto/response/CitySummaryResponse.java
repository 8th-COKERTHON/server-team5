package com.cotato.cokerthon.domain.sleep.dto.response;

import com.cotato.cokerthon.domain.city.entity.City;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보딩패스에 표시되는 도시 정보 (출발지 서울 또는 매칭된 현재 수면 도시)")
public record CitySummaryResponse(
	@Schema(description = "국가명", example = "인도")
	String countryName,

	@Schema(description = "도시명 (한글)", example = "뉴델리")
	String cityNameKr,

	@Schema(description = "영문 도시명", example = "NEW DELHI")
	String cityNameEn,

	@Schema(description = "위도 (프론트 지구본 매핑용)", example = "28.6139")
	double latitude,

	@Schema(description = "경도 (프론트 지구본 매핑용)", example = "77.2090")
	double longitude
) {

	public static CitySummaryResponse from(City city) {
		return new CitySummaryResponse(
			city.getCountryName(),
			city.getCityNameKr(),
			city.getCityNameEn(),
			city.getLatitude(),
			city.getLongitude()
		);
	}
}
