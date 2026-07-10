package com.cotato.cokerthon.domain.route.service;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.route.dto.response.ArrivalResponse;
import com.cotato.cokerthon.domain.route.dto.response.BoardingPassResponse;
import com.cotato.cokerthon.domain.route.dto.response.CitySummaryResponse;
import com.cotato.cokerthon.domain.route.dto.response.ReturnRouteResponse;
import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStatus;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStop;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteDayRepository;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteRepository;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteStopRepository;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.entity.SleepRecord;
import com.cotato.cokerthon.domain.sleep.repository.SleepJetlagResultRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReturnRouteService {

	private static final Logger log = LoggerFactory.getLogger(ReturnRouteService.class);
	private static final int DAILY_ADJUST_MINUTES = 30;
	private static final int SEOUL_FALLBACK_GAP_MINUTES = 15;

	private final ReturnRouteRepository returnRouteRepository;
	private final ReturnRouteDayRepository returnRouteDayRepository;
	private final ReturnRouteStopRepository returnRouteStopRepository;
	private final SleepJetlagResultRepository sleepJetlagResultRepository;
	private final MemberRepository memberRepository;
	private final CityRepository cityRepository;
	private final SleepTimeCalculator sleepTimeCalculator;

	public ReturnRouteService(ReturnRouteRepository returnRouteRepository,
		ReturnRouteDayRepository returnRouteDayRepository,
		ReturnRouteStopRepository returnRouteStopRepository,
		SleepJetlagResultRepository sleepJetlagResultRepository,
		MemberRepository memberRepository,
		CityRepository cityRepository,
		SleepTimeCalculator sleepTimeCalculator) {
		this.returnRouteRepository = returnRouteRepository;
		this.returnRouteDayRepository = returnRouteDayRepository;
		this.returnRouteStopRepository = returnRouteStopRepository;
		this.sleepJetlagResultRepository = sleepJetlagResultRepository;
		this.memberRepository = memberRepository;
		this.cityRepository = cityRepository;
		this.sleepTimeCalculator = sleepTimeCalculator;
	}

	@Transactional
	public ReturnRouteResponse createReturnRoute(Long memberId, Long resultId) {
		Member member = getMember(memberId);
		SleepJetlagResult result = sleepJetlagResultRepository.findById(resultId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SLEEP_RESULT_NOT_FOUND));
		validateOwner(member, result);

		returnRouteRepository.findAllByMemberAndStatus(member, ReturnRouteStatus.IN_PROGRESS)
			.forEach(ReturnRoute::cancel);

		List<GeneratedRouteDay> generatedDays = generateRouteDays(result);
		ReturnRoute route = returnRouteRepository.save(
			ReturnRoute.create(member, result, DAILY_ADJUST_MINUTES, generatedDays.size())
		);

		returnRouteStopRepository.save(ReturnRouteStop.create(route, result.getMatchedCity(), 0));

		List<ReturnRouteDay> savedDays = new ArrayList<>();
		for (GeneratedRouteDay generatedDay : generatedDays) {
			ReturnRouteDay day = ReturnRouteDay.create(
				route,
				generatedDay.dayNumber(),
				generatedDay.checkpointCity(),
				generatedDay.currentBedtime(),
				generatedDay.currentWaketime(),
				generatedDay.targetBedtime(),
				generatedDay.targetWaketime(),
				generatedDay.targetBedtime().minusHours(8),
				generatedDay.targetBedtime().minusHours(1),
				generatedDay.targetBedtime().minusMinutes(15),
				generatedDay.targetBedtime().plusMinutes(15)
			);
			savedDays.add(returnRouteDayRepository.save(day));
			returnRouteStopRepository.save(ReturnRouteStop.create(route, generatedDay.checkpointCity(),
				generatedDay.dayNumber()));
		}

		return ReturnRouteResponse.of(route, CitySummaryResponse.from(result.getMatchedCity()), savedDays.get(0),
			savedDays);
	}

	public ReturnRouteResponse getCurrentRoute(Long memberId) {
		ReturnRoute route = getCurrentRouteEntity(memberId);
		List<ReturnRouteDay> days = returnRouteDayRepository.findAllByReturnRouteOrderByDayNumberAsc(route);
		ReturnRouteDay currentDay = getCurrentDay(route);

		return ReturnRouteResponse.of(route, getDepartureCity(route), currentDay, days);
	}

	public BoardingPassResponse getCurrentBoardingPass(Long memberId) {
		ReturnRoute route = getCurrentRouteEntity(memberId);
		ReturnRouteDay currentDay = getCurrentDay(route);

		return BoardingPassResponse.of(
			route.getId(),
			route.getResult().getResultDate().plusDays(currentDay.getDayNumber() - 1L),
			getDepartureCity(route),
			currentDay
		);
	}

	@Transactional
	public ArrivalResponse sleepAndArrive(Long memberId) {
		ReturnRoute route = getCurrentRouteEntity(memberId);
		ReturnRouteDay currentDay = getCurrentDay(route);
		City arrivedCity = currentDay.getCheckpointCity();

		route.arriveNextStop();

		return new ArrivalResponse(
			route.getId(),
			route.getCurrentDayNumber(),
			route.getStatus(),
			CitySummaryResponse.from(arrivedCity)
		);
	}

	private List<GeneratedRouteDay> generateRouteDays(SleepJetlagResult result) {
		SleepRecord sleepRecord = result.getSleepRecord();
		LocalTime currentBedtime = sleepRecord.getCurrentBedtime();
		LocalTime currentWaketime = sleepRecord.getCurrentWaketime();
		LocalTime targetBedtime = sleepRecord.getTargetBedtime();
		LocalTime targetWaketime = sleepRecord.getTargetWaketime();
		List<GeneratedRouteDay> routeDays = new ArrayList<>();

		int dayNumber = 1;
		while (!sleepTimeCalculator.reached(currentBedtime, currentWaketime, targetBedtime, targetWaketime)) {
			LocalTime nextBedtime = sleepTimeCalculator.moveTowardTarget(currentBedtime, targetBedtime);
			LocalTime nextWaketime = sleepTimeCalculator.moveTowardTarget(currentWaketime, targetWaketime);
			LocalTime nextMidTime = sleepTimeCalculator.calculateMidTime(nextBedtime, nextWaketime);
			int signedGapMinutes = sleepTimeCalculator.signedMidDifferenceMinutes(result.getTargetMidTime(),
				nextMidTime);
			City checkpointCity = pickRandomCity(signedGapMinutes);

			routeDays.add(new GeneratedRouteDay(dayNumber, checkpointCity, currentBedtime, currentWaketime,
				nextBedtime, nextWaketime));

			currentBedtime = nextBedtime;
			currentWaketime = nextWaketime;
			dayNumber++;
		}

		if (routeDays.isEmpty() || routeDays.get(routeDays.size() - 1).checkpointCity().getDirection() != CityDirection.BASE) {
			City seoul = getSeoulCity();
			routeDays.add(new GeneratedRouteDay(dayNumber, seoul, currentBedtime, currentWaketime,
				targetBedtime, targetWaketime));
		}

		return routeDays;
	}

	private City pickRandomCity(int signedGapMinutes) {
		CityDirection direction = toDirection(signedGapMinutes);
		int gapMinutes = Math.abs(signedGapMinutes);
		List<City> candidates;

		if (direction == CityDirection.BASE || gapMinutes <= SEOUL_FALLBACK_GAP_MINUTES) {
			return getSeoulCity();
		}

		candidates = cityRepository.findAllByDirectionAndGapMinutes(direction, gapMinutes);
		if (candidates.isEmpty()) {
			String message = "귀국 루트 경유 도시 후보를 찾을 수 없습니다. direction=%s, gapMinutes=%d"
				.formatted(direction, gapMinutes);
			log.warn(message);
			throw new BusinessException(ErrorCode.RETURN_ROUTE_CITY_NOT_FOUND, message);
		}

		return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
	}

	private CityDirection toDirection(int signedGapMinutes) {
		if (signedGapMinutes > 0) {
			return CityDirection.WEST;
		}
		if (signedGapMinutes < 0) {
			return CityDirection.EAST;
		}
		return CityDirection.BASE;
	}

	private City getSeoulCity() {
		return cityRepository.findFirstByDirectionOrderByDisplayOrderAsc(CityDirection.BASE)
			.orElseThrow(() -> new BusinessException(ErrorCode.SEOUL_CITY_NOT_FOUND));
	}

	private ReturnRoute getCurrentRouteEntity(Long memberId) {
		Member member = getMember(memberId);
		return returnRouteRepository.findFirstByMemberAndStatusOrderByCreatedAtDesc(member,
				ReturnRouteStatus.IN_PROGRESS)
			.orElseThrow(() -> new BusinessException(ErrorCode.RETURN_ROUTE_NOT_FOUND));
	}

	private ReturnRouteDay getCurrentDay(ReturnRoute route) {
		return returnRouteDayRepository.findByReturnRouteAndDayNumber(route, route.getCurrentDayNumber())
			.orElseThrow(() -> new BusinessException(ErrorCode.RETURN_ROUTE_DAY_NOT_FOUND));
	}

	private CitySummaryResponse getDepartureCity(ReturnRoute route) {
		if (route.getCurrentDayNumber() <= 1) {
			return CitySummaryResponse.from(route.getResult().getMatchedCity());
		}

		ReturnRouteDay previousDay = returnRouteDayRepository.findByReturnRouteAndDayNumber(route,
				route.getCurrentDayNumber() - 1)
			.orElseThrow(() -> new BusinessException(ErrorCode.RETURN_ROUTE_DAY_NOT_FOUND));
		return CitySummaryResponse.from(previousDay.getCheckpointCity());
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
	}

	private void validateOwner(Member member, SleepJetlagResult result) {
		if (!result.getMember().getId().equals(member.getId())) {
			throw new BusinessException(ErrorCode.SLEEP_RESULT_FORBIDDEN);
		}
	}

	private record GeneratedRouteDay(
		int dayNumber,
		City checkpointCity,
		LocalTime currentBedtime,
		LocalTime currentWaketime,
		LocalTime targetBedtime,
		LocalTime targetWaketime
	) {
	}
}
