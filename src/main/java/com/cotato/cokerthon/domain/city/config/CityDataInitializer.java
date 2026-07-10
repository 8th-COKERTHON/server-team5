package com.cotato.cokerthon.domain.city.config;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// 서울 기준 시차 매핑용 도시 마스터 데이터 시드. 앱 기동 시 비어 있으면 1회 채워 넣음
//
// gap_minutes는 "취침~기상 중간 시각"끼리의 원형(circular) 거리를 기준으로 계산했다 (최대 12시간=720분).
// 날짜 없이 시각(TIME)만 비교하기 때문에 실제 관용적으로 알려진 시차(예: 한국-뉴욕 14시간)가 아니라
// 항상 더 짧은 쪽 경로(예: 뉴욕 10시간)로 환산된다. 그 결과 미국처럼 관용적으로는 "서쪽"으로 인식되는
// 도시도 이 앱에서는 EAST 버킷에 들어갈 수 있다 (방향은 실제 지리적 방향이 아니라 매칭용 내부 값).
//
// PM 정리본(국가별 대표 도시/위경도) 반영 시, 같은 UTC 오프셋에 이미 대표 국가가 있으면 스킵하고
// (국가+오프셋 기준 중복 제거) 새 국가·도시만 추가했다. 매핑 구간 경계는 방향별 전체 gap_minutes
// 목록을 다시 정렬해 재계산했다.
@Component
@Order(1)
public class CityDataInitializer implements ApplicationRunner {

	private final CityRepository cityRepository;

	public CityDataInitializer(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (cityRepository.count() > 0) {
			return;
		}

		cityRepository.saveAll(seedCities());
	}

	private List<City> seedCities() {
		return List.of(
			// 기준 도시 (시차 30분 미만은 서울과 동일한 것으로 취급)
			City.create("대한민국", "서울", "SEOUL", "ICN",
				"+09:00", "Asia/Seoul", CityDirection.BASE, 0, 0, 30, false, 0, 37.5665, 126.9780),

			// ── 서쪽 방향 (gap_minutes 오름차순) ──
			City.create("중국", "베이징", "BEIJING", "PEK",
				"+08:00", "Asia/Shanghai", CityDirection.WEST, 60, 30, 90, false, 1, 39.9042, 116.4074),
			City.create("싱가포르", "싱가포르", "SINGAPORE", "SIN",
				"+08:00", "Asia/Singapore", CityDirection.WEST, 60, 30, 90, false, 2, 1.3521, 103.8198),
			City.create("대만", "타이베이", "TAIPEI", "TPE",
				"+08:00", "Asia/Taipei", CityDirection.WEST, 60, 30, 90, false, 3, 25.0500, 121.5000),

			City.create("태국", "방콕", "BANGKOK", "BKK",
				"+07:00", "Asia/Bangkok", CityDirection.WEST, 120, 90, 135, false, 4, 13.7563, 100.5018),
			City.create("인도네시아", "자카르타", "JAKARTA", "CGK",
				"+07:00", "Asia/Jakarta", CityDirection.WEST, 120, 90, 135, false, 5, -6.2088, 106.8456),
			City.create("베트남", "호찌민", "HO CHI MINH", "SGN",
				"+07:00", "Asia/Ho_Chi_Minh", CityDirection.WEST, 120, 90, 135, false, 6, 10.7500, 106.6667),

			City.create("미얀마", "양곤", "YANGON", "RGN",
				"+06:30", "Asia/Yangon", CityDirection.WEST, 150, 135, 165, false, 7, 16.8409, 96.1735),
			City.create("코코스제도", "코코스제도", "COCOS ISLANDS", "CCK",
				"+06:30", "Indian/Cocos", CityDirection.WEST, 150, 135, 165, false, 8, -12.1667, 96.9167),

			City.create("방글라데시", "다카", "DHAKA", "DAC",
				"+06:00", "Asia/Dhaka", CityDirection.WEST, 180, 165, 188, false, 9, 23.7167, 90.4167),
			City.create("부탄", "팀푸", "THIMPHU", "PBH",
				"+06:00", "Asia/Thimphu", CityDirection.WEST, 180, 165, 188, false, 10, 27.4667, 89.6500),
			City.create("키르기스스탄", "비슈케크", "BISHKEK", "FRU",
				"+06:00", "Asia/Bishkek", CityDirection.WEST, 180, 165, 188, false, 11, 42.9000, 74.6000),

			City.create("네팔", "카트만두", "KATHMANDU", "KTM",
				"+05:45", "Asia/Kathmandu", CityDirection.WEST, 195, 188, 203, false, 12, 27.7172, 85.3240),

			City.create("인도", "뉴델리", "NEW DELHI", "DEL",
				"+05:30", "Asia/Kolkata", CityDirection.WEST, 210, 203, 225, false, 13, 28.6139, 77.2090),
			City.create("스리랑카", "콜롬보", "COLOMBO", "CMB",
				"+05:30", "Asia/Colombo", CityDirection.WEST, 210, 203, 225, false, 14, 6.9333, 79.8500),

			City.create("파키스탄", "카라치", "KARACHI", "KHI",
				"+05:00", "Asia/Karachi", CityDirection.WEST, 240, 225, 255, false, 15, 24.8667, 67.0500),
			City.create("몰디브", "말레", "MALE", "MLE",
				"+05:00", "Indian/Maldives", CityDirection.WEST, 240, 225, 255, false, 16, 4.1667, 73.5000),
			City.create("카자흐스탄", "알마티", "ALMATY", "ALA",
				"+05:00", "Asia/Almaty", CityDirection.WEST, 240, 225, 255, false, 17, 43.2500, 76.9500),

			City.create("아프가니스탄", "카불", "KABUL", "KBL",
				"+04:30", "Asia/Kabul", CityDirection.WEST, 270, 255, 285, false, 18, 34.5167, 69.2000),

			City.create("아랍에미리트", "두바이", "DUBAI", "DXB",
				"+04:00", "Asia/Dubai", CityDirection.WEST, 300, 285, 315, false, 19, 25.2048, 55.2708),
			City.create("오만", "무스카트", "MUSCAT", "MCT",
				"+04:00", "Asia/Muscat", CityDirection.WEST, 300, 285, 315, false, 20, 23.6000, 58.5833),
			City.create("조지아", "트빌리시", "TBILISI", "TBS",
				"+04:00", "Asia/Tbilisi", CityDirection.WEST, 300, 285, 315, false, 21, 41.7167, 44.8167),

			City.create("이란", "테헤란", "TEHRAN", "IKA",
				"+03:30", "Asia/Tehran", CityDirection.WEST, 330, 315, 345, false, 22, 35.6667, 51.4333),

			City.create("러시아", "모스크바", "MOSCOW", "SVO",
				"+03:00", "Europe/Moscow", CityDirection.WEST, 360, 345, 390, false, 23, 55.7558, 37.6173),
			City.create("튀르키예", "이스탄불", "ISTANBUL", "IST",
				"+03:00", "Europe/Istanbul", CityDirection.WEST, 360, 345, 390, false, 24, 41.0167, 28.9667),
			City.create("사우디아라비아", "리야드", "RIYADH", "RUH",
				"+03:00", "Asia/Riyadh", CityDirection.WEST, 360, 345, 390, false, 25, 24.6333, 46.7167),

			City.create("이집트", "카이로", "CAIRO", "CAI",
				"+02:00", "Africa/Cairo", CityDirection.WEST, 420, 390, 450, true, 26, 30.0444, 31.2357),
			City.create("남아프리카공화국", "요하네스버그", "JOHANNESBURG", "JNB",
				"+02:00", "Africa/Johannesburg", CityDirection.WEST, 420, 390, 450, true, 27, -26.2500, 28.0000),
			City.create("그리스", "아테네", "ATHENS", "ATH",
				"+02:00", "Europe/Athens", CityDirection.WEST, 420, 390, 450, true, 28, 37.9667, 23.7167),

			City.create("프랑스", "파리", "PARIS", "CDG",
				"+01:00", "Europe/Paris", CityDirection.WEST, 480, 450, 510, true, 29, 48.8566, 2.3522),
			City.create("독일", "베를린", "BERLIN", "BER",
				"+01:00", "Europe/Berlin", CityDirection.WEST, 480, 450, 510, true, 30, 52.5200, 13.4050),
			City.create("이탈리아", "로마", "ROME", "FCO",
				"+01:00", "Europe/Rome", CityDirection.WEST, 480, 450, 510, true, 31, 41.9000, 12.4833),

			City.create("영국", "런던", "LONDON", "LHR",
				"+00:00", "Europe/London", CityDirection.WEST, 540, 510, 570, true, 32, 51.5074, -0.1278),
			City.create("포르투갈", "리스본", "LISBON", "LIS",
				"+00:00", "Europe/Lisbon", CityDirection.WEST, 540, 510, 570, true, 33, 38.7223, -9.1393),
			City.create("아이슬란드", "레이캬비크", "REYKJAVIK", "KEF",
				"+00:00", "Atlantic/Reykjavik", CityDirection.WEST, 540, 510, 570, true, 34, 64.1500, -21.8500),

			City.create("포르투갈", "아조레스제도", "AZORES", "PDL",
				"-01:00", "Atlantic/Azores", CityDirection.WEST, 600, 570, null, true, 35, 37.7333, -25.6667),
			City.create("카보베르데", "프라이아", "PRAIA", "RAI",
				"-01:00", "Atlantic/Cape_Verde", CityDirection.WEST, 600, 570, null, true, 36, 14.9167, -23.5167),

			// ── 동쪽 방향 (gap_minutes 오름차순) ──
			City.create("호주", "다윈", "DARWIN", "DRW",
				"+09:30", "Australia/Darwin", CityDirection.EAST, 30, 15, 45, false, 37, -12.4667, 130.8333),

			City.create("호주", "시드니", "SYDNEY", "SYD",
				"+10:00", "Australia/Sydney", CityDirection.EAST, 60, 45, 75, false, 38, -33.8688, 151.2093),
			City.create("호주", "멜버른", "MELBOURNE", "MEL",
				"+10:00", "Australia/Melbourne", CityDirection.EAST, 60, 45, 75, false, 39, -37.8136, 144.9631),
			City.create("파푸아뉴기니", "포트모르즈비", "PORT MORESBY", "POM",
				"+10:00", "Pacific/Port_Moresby", CityDirection.EAST, 60, 45, 75, false, 40, -9.5000, 147.1667),
			City.create("괌", "괌", "GUAM", "GUM",
				"+10:00", "Pacific/Guam", CityDirection.EAST, 60, 45, 75, false, 41, 13.4667, 144.7500),

			City.create("호주", "로드하우섬", "LORD HOWE ISLAND", "LDH",
				"+10:30", "Australia/Lord_Howe", CityDirection.EAST, 90, 75, 105, false, 42, -31.5500, 159.0833),

			City.create("솔로몬제도", "호니아라", "HONIARA", "HIR",
				"+11:00", "Pacific/Guadalcanal", CityDirection.EAST, 120, 105, 150, false, 43, -9.4438, 159.9729),
			City.create("바누아투", "포트빌라", "PORT VILA", "VLI",
				"+11:00", "Pacific/Efate", CityDirection.EAST, 120, 105, 150, false, 44, -17.6667, 168.4167),
			City.create("뉴칼레도니아", "누메아", "NOUMEA", "NOU",
				"+11:00", "Pacific/Noumea", CityDirection.EAST, 120, 105, 150, false, 45, -22.2667, 166.4500),

			City.create("뉴질랜드", "오클랜드", "AUCKLAND", "AKL",
				"+12:00", "Pacific/Auckland", CityDirection.EAST, 180, 150, 210, false, 46, -36.8485, 174.7633),
			City.create("피지", "수바", "SUVA", "SUV",
				"+12:00", "Pacific/Fiji", CityDirection.EAST, 180, 150, 210, false, 47, -18.1333, 178.4167),
			City.create("러시아", "캄차카", "KAMCHATKA", "PKC",
				"+12:00", "Asia/Kamchatka", CityDirection.EAST, 180, 150, 210, false, 48, 53.0167, 158.6500),

			City.create("통가", "누쿠알로파", "NUKU'ALOFA", "TBU",
				"+13:00", "Pacific/Tongatapu", CityDirection.EAST, 240, 210, 270, false, 49, -21.1789, -175.1982),
			City.create("키리바시", "칸톤섬", "KANTON", "KNT",
				"+13:00", "Pacific/Kanton", CityDirection.EAST, 240, 210, 270, false, 50, -2.7833, -171.7167),

			City.create("미국", "호놀룰루", "HONOLULU", "HNL",
				"-10:00", "Pacific/Honolulu", CityDirection.EAST, 300, 270, 360, true, 51, 21.3069, -157.8583),
			City.create("키리바시", "키리티마티", "KIRITIMATI", "CXI",
				"+14:00", "Pacific/Kiritimati", CityDirection.EAST, 300, 270, 360, true, 52, 1.8667, -157.3333),

			City.create("미국", "로스앤젤레스", "LOS ANGELES", "LAX",
				"-08:00", "America/Los_Angeles", CityDirection.EAST, 420, 360, 480, true, 53, 34.0522, -118.2437),
			City.create("캐나다", "밴쿠버", "VANCOUVER", "YVR",
				"-08:00", "America/Vancouver", CityDirection.EAST, 420, 360, 480, true, 54, 49.2827, -123.1207),

			City.create("미국", "시카고", "CHICAGO", "ORD",
				"-06:00", "America/Chicago", CityDirection.EAST, 540, 480, 570, true, 55, 41.8781, -87.6298),

			City.create("미국", "뉴욕", "NEW YORK", "JFK",
				"-05:00", "America/New_York", CityDirection.EAST, 600, 570, null, true, 56, 40.7128, -74.0060),
			City.create("캐나다", "토론토", "TORONTO", "YYZ",
				"-05:00", "America/Toronto", CityDirection.EAST, 600, 570, null, true, 57, 43.6532, -79.3832)
		);
	}
}
