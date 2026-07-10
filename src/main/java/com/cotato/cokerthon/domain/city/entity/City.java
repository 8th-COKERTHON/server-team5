package com.cotato.cokerthon.domain.city.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 도시 매핑 마스터 데이터. 같은 시차 구간에 여러 도시가 매핑될 수 있어(랜덤 매칭) 시드 데이터라도 조합 수가 늘어날 수 있음
@Entity
@Table(name = "cities")
public class City {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// 국가명 (예: 인도)
	@Column(nullable = false, length = 50)
	private String countryName;

	// 도시명 한글 (예: 뉴델리)
	@Column(nullable = false, length = 50)
	private String cityNameKr;

	// 영문 도시명 (예: NEW DELHI)
	@Column(nullable = false, length = 50)
	private String cityNameEn;

	// IATA 공항 코드 (예: DEL)
	@Column(nullable = false, unique = true, length = 3)
	private String airportCode;

	// UTC 오프셋 (예: +05:30)
	@Column(nullable = false, length = 10)
	private String utcOffset;

	// IANA 시간대 ID
	@Column(nullable = false, length = 50)
	private String ianaTimezoneId;

	// 서울 기준 방향
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private CityDirection direction;

	// 서울과의 시차 절대값(분)
	@Column(nullable = false)
	private int gapMinutes;

	// 매핑 구간 최소값(분)
	@Column(nullable = false)
	private int mappingMinMinutes;

	// 매핑 구간 최대값(분), null이면 장거리 무제한
	private Integer mappingMaxMinutes;

	// 6시간30분 이상 장거리 여부
	@Column(name = "is_long_haul", nullable = false)
	private boolean longHaul;

	// 국기 이미지 URL
	@Column(length = 500)
	private String flagImageUrl;

	// 정렬/귀국 경유 순서 기준 (서울과 가까운 순)
	@Column(nullable = false)
	private int displayOrder;

	// 위도 (프론트 지구본 매핑용)
	@Column(nullable = false)
	private double latitude;

	// 경도 (프론트 지구본 매핑용)
	@Column(nullable = false)
	private double longitude;

	protected City() {
	}

	private City(String countryName, String cityNameKr, String cityNameEn, String airportCode,
		String utcOffset, String ianaTimezoneId, CityDirection direction, int gapMinutes,
		int mappingMinMinutes, Integer mappingMaxMinutes, boolean longHaul,
		int displayOrder, double latitude, double longitude) {
		this.countryName = countryName;
		this.cityNameKr = cityNameKr;
		this.cityNameEn = cityNameEn;
		this.airportCode = airportCode;
		this.utcOffset = utcOffset;
		this.ianaTimezoneId = ianaTimezoneId;
		this.direction = direction;
		this.gapMinutes = gapMinutes;
		this.mappingMinMinutes = mappingMinMinutes;
		this.mappingMaxMinutes = mappingMaxMinutes;
		this.longHaul = longHaul;
		this.displayOrder = displayOrder;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static City create(String countryName, String cityNameKr, String cityNameEn, String airportCode,
		String utcOffset, String ianaTimezoneId, CityDirection direction, int gapMinutes,
		int mappingMinMinutes, Integer mappingMaxMinutes, boolean longHaul,
		int displayOrder, double latitude, double longitude) {
		return new City(countryName, cityNameKr, cityNameEn, airportCode, utcOffset, ianaTimezoneId,
			direction, gapMinutes, mappingMinMinutes, mappingMaxMinutes, longHaul,
			displayOrder, latitude, longitude);
	}

	public Integer getId() {
		return id;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getCityNameKr() {
		return cityNameKr;
	}

	public String getCityNameEn() {
		return cityNameEn;
	}

	public String getAirportCode() {
		return airportCode;
	}

	public String getUtcOffset() {
		return utcOffset;
	}

	public String getIanaTimezoneId() {
		return ianaTimezoneId;
	}

	public CityDirection getDirection() {
		return direction;
	}

	public int getGapMinutes() {
		return gapMinutes;
	}

	public int getMappingMinMinutes() {
		return mappingMinMinutes;
	}

	public Integer getMappingMaxMinutes() {
		return mappingMaxMinutes;
	}

	public boolean isLongHaul() {
		return longHaul;
	}

	public String getFlagImageUrl() {
		return flagImageUrl;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
