package com.cotato.cokerthon.domain.sleep.service;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.sleep.dto.request.SleepJetlagRequest;
import com.cotato.cokerthon.domain.sleep.dto.response.SleepJetlagResultResponse;
import com.cotato.cokerthon.domain.sleep.entity.JetlagDirection;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.entity.SleepRecord;
import com.cotato.cokerthon.domain.sleep.repository.SleepJetlagResultRepository;
import com.cotato.cokerthon.domain.sleep.repository.SleepRecordRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SleepJetlagService {

	private static final int MINUTES_PER_DAY = 24 * 60;
	private static final int HALF_DAY_MINUTES = MINUTES_PER_DAY / 2;

	private final MemberRepository memberRepository;
	private final CityRepository cityRepository;
	private final SleepRecordRepository sleepRecordRepository;
	private final SleepJetlagResultRepository sleepJetlagResultRepository;

	public SleepJetlagService(
		MemberRepository memberRepository,
		CityRepository cityRepository,
		SleepRecordRepository sleepRecordRepository,
		SleepJetlagResultRepository sleepJetlagResultRepository
	) {
		this.memberRepository = memberRepository;
		this.cityRepository = cityRepository;
		this.sleepRecordRepository = sleepRecordRepository;
		this.sleepJetlagResultRepository = sleepJetlagResultRepository;
	}

	@Transactional
	public SleepJetlagResultResponse calculate(Long memberId, SleepJetlagRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		MidSleep currentSleep = midSleepOf(request.currentBedtime(), request.currentWaketime());
		MidSleep targetSleep = midSleepOf(request.targetBedtime(), request.targetWaketime());

		SleepRecord sleepRecord = sleepRecordRepository.save(SleepRecord.create(
			member,
			request.currentBedtime(), request.currentWaketime(), currentSleep.sleepMinutes(),
			request.targetBedtime(), request.targetWaketime(), targetSleep.sleepMinutes()
		));

		Jetlag jetlag = calculateJetlag(currentSleep.midTime(), targetSleep.midTime());
		City matchedCity = findMatchedCity(jetlag.direction(), jetlag.minutes());

		SleepJetlagResult result = sleepJetlagResultRepository.save(SleepJetlagResult.create(
			sleepRecord, member, currentSleep.midTime(), targetSleep.midTime(),
			jetlag.minutes(), jetlag.direction(), matchedCity, LocalDate.now()
		));

		City seoul = cityRepository.findByDirection(CityDirection.BASE)
			.orElseThrow(() -> new BusinessException(ErrorCode.CITY_NOT_MATCHED));

		return SleepJetlagResultResponse.from(result, seoul);
	}

	// 취침~기상 구간의 중간 시각과 총 수면시간을 계산 (자정을 넘기는 구간도 처리)
	private MidSleep midSleepOf(LocalTime bedtime, LocalTime waketime) {
		int bedMinutes = bedtime.toSecondOfDay() / 60;
		int wakeMinutes = waketime.toSecondOfDay() / 60;
		int sleepMinutes = wakeMinutes - bedMinutes;
		if (sleepMinutes <= 0) {
			sleepMinutes += MINUTES_PER_DAY;
		}

		return new MidSleep(bedtime.plusMinutes(sleepMinutes / 2), sleepMinutes);
	}

	// 현재/목표 중간 수면시각의 차이(절댓값)를 시차로, 부호를 방향으로 변환
	private Jetlag calculateJetlag(LocalTime currentMidTime, LocalTime targetMidTime) {
		int diff = (currentMidTime.toSecondOfDay() - targetMidTime.toSecondOfDay()) / 60;
		if (diff > HALF_DAY_MINUTES) {
			diff -= MINUTES_PER_DAY;
		} else if (diff < -HALF_DAY_MINUTES) {
			diff += MINUTES_PER_DAY;
		}

		JetlagDirection direction = diff > 0 ? JetlagDirection.WEST
			: diff < 0 ? JetlagDirection.EAST
			: JetlagDirection.SAME;

		return new Jetlag(Math.abs(diff), direction);
	}

	// 같은 시차 구간에 여러 도시가 있으면 그중 하나를 랜덤으로 매칭
	private City findMatchedCity(JetlagDirection direction, int jetlagMinutes) {
		CityDirection cityDirection = direction == JetlagDirection.SAME
			? CityDirection.BASE
			: CityDirection.valueOf(direction.name());

		List<City> matchedCities = cityRepository.findAllByDirectionAndGapMinutes(cityDirection, jetlagMinutes);
		if (matchedCities.isEmpty()) {
			throw new BusinessException(ErrorCode.CITY_NOT_MATCHED);
		}

		return matchedCities.get(ThreadLocalRandom.current().nextInt(matchedCities.size()));
	}

	private record MidSleep(LocalTime midTime, int sleepMinutes) {
	}

	private record Jetlag(int minutes, JetlagDirection direction) {
	}
}
