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

			// 서쪽 방향 - 같은 시차 구간에 여러 도시를 두어 랜덤 매칭이 실제로 일어나도록 구성
			City.create("중국", "베이징", "BEIJING", "PEK",
				"+08:00", "Asia/Shanghai", CityDirection.WEST, 60, 30, 90, false, 1, 39.9042, 116.4074),
			City.create("싱가포르", "싱가포르", "SINGAPORE", "SIN",
				"+08:00", "Asia/Singapore", CityDirection.WEST, 60, 30, 90, false, 2, 1.3521, 103.8198),

			City.create("태국", "방콕", "BANGKOK", "BKK",
				"+07:00", "Asia/Bangkok", CityDirection.WEST, 120, 90, 135, false, 3, 13.7563, 100.5018),
			City.create("인도네시아", "자카르타", "JAKARTA", "CGK",
				"+07:00", "Asia/Jakarta", CityDirection.WEST, 120, 90, 135, false, 4, -6.2088, 106.8456),

			City.create("미얀마", "양곤", "YANGON", "RGN",
				"+06:30", "Asia/Yangon", CityDirection.WEST, 150, 135, 173, false, 5, 16.8409, 96.1735),
			City.create("네팔", "카트만두", "KATHMANDU", "KTM",
				"+05:45", "Asia/Kathmandu", CityDirection.WEST, 195, 173, 203, false, 6, 27.7172, 85.3240),
			City.create("인도", "뉴델리", "NEW DELHI", "DEL",
				"+05:30", "Asia/Kolkata", CityDirection.WEST, 210, 203, 255, false, 7, 28.6139, 77.2090),
			City.create("아랍에미리트", "두바이", "DUBAI", "DXB",
				"+04:00", "Asia/Dubai", CityDirection.WEST, 300, 255, 330, false, 8, 25.2048, 55.2708),
			City.create("러시아", "모스크바", "MOSCOW", "SVO",
				"+03:00", "Europe/Moscow", CityDirection.WEST, 360, 330, 390, false, 9, 55.7558, 37.6173),
			City.create("이집트", "카이로", "CAIRO", "CAI",
				"+02:00", "Africa/Cairo", CityDirection.WEST, 420, 390, 450, true, 10, 30.0444, 31.2357),

			City.create("프랑스", "파리", "PARIS", "CDG",
				"+01:00", "Europe/Paris", CityDirection.WEST, 480, 450, 510, true, 11, 48.8566, 2.3522),
			City.create("독일", "베를린", "BERLIN", "BER",
				"+01:00", "Europe/Berlin", CityDirection.WEST, 480, 450, 510, true, 12, 52.5200, 13.4050),

			City.create("영국", "런던", "LONDON", "LHR",
				"+00:00", "Europe/London", CityDirection.WEST, 540, 510, null, true, 13, 51.5074, -0.1278),
			City.create("포르투갈", "리스본", "LISBON", "LIS",
				"+00:00", "Europe/Lisbon", CityDirection.WEST, 540, 510, null, true, 14, 38.7223, -9.1393),

			// 동쪽 방향
			City.create("호주", "시드니", "SYDNEY", "SYD",
				"+10:00", "Australia/Sydney", CityDirection.EAST, 60, 30, 90, false, 15, -33.8688, 151.2093),
			City.create("호주", "멜버른", "MELBOURNE", "MEL",
				"+10:00", "Australia/Melbourne", CityDirection.EAST, 60, 30, 90, false, 16, -37.8136, 144.9631),

			City.create("솔로몬제도", "호니아라", "HONIARA", "HIR",
				"+11:00", "Pacific/Guadalcanal", CityDirection.EAST, 120, 90, 150, false, 17, -9.4438, 159.9729),
			City.create("뉴질랜드", "오클랜드", "AUCKLAND", "AKL",
				"+12:00", "Pacific/Auckland", CityDirection.EAST, 180, 150, 210, false, 18, -36.8485, 174.7633),
			City.create("통가", "누쿠알로파", "NUKU'ALOFA", "TBU",
				"+13:00", "Pacific/Tongatapu", CityDirection.EAST, 240, 210, 270, false, 19, -21.1789, -175.1982),
			City.create("미국", "호놀룰루", "HONOLULU", "HNL",
				"-10:00", "Pacific/Honolulu", CityDirection.EAST, 300, 270, 360, true, 20, 21.3069, -157.8583),

			City.create("미국", "로스앤젤레스", "LOS ANGELES", "LAX",
				"-08:00", "America/Los_Angeles", CityDirection.EAST, 420, 360, 480, true, 21, 34.0522, -118.2437),
			City.create("캐나다", "밴쿠버", "VANCOUVER", "YVR",
				"-08:00", "America/Vancouver", CityDirection.EAST, 420, 360, 480, true, 22, 49.2827, -123.1207),

			City.create("미국", "시카고", "CHICAGO", "ORD",
				"-06:00", "America/Chicago", CityDirection.EAST, 540, 480, 570, true, 23, 41.8781, -87.6298),

			City.create("미국", "뉴욕", "NEW YORK", "JFK",
				"-05:00", "America/New_York", CityDirection.EAST, 600, 570, null, true, 24, 40.7128, -74.0060),
			City.create("캐나다", "토론토", "TORONTO", "YYZ",
				"-05:00", "America/Toronto", CityDirection.EAST, 600, 570, null, true, 25, 43.6532, -79.3832)
		);
	}
}
