package com.cotato.cokerthon.domain.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStatus;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteDayRepository;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteRepository;
import com.cotato.cokerthon.domain.route.repository.ReturnRouteStopRepository;
import com.cotato.cokerthon.domain.sleep.entity.JetlagDirection;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.entity.SleepRecord;
import com.cotato.cokerthon.domain.sleep.repository.SleepJetlagResultRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReturnRouteServiceTest {

	private static final Long MEMBER_ID = 1L;
	private static final Long RESULT_ID = 10L;

	@Mock
	private ReturnRouteRepository returnRouteRepository;

	@Mock
	private ReturnRouteDayRepository returnRouteDayRepository;

	@Mock
	private ReturnRouteStopRepository returnRouteStopRepository;

	@Mock
	private SleepJetlagResultRepository sleepJetlagResultRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CityRepository cityRepository;

	@Mock
	private SleepTimeCalculator sleepTimeCalculator;

	private ReturnRouteService returnRouteService;

	@BeforeEach
	void setUp() {
		returnRouteService = new ReturnRouteService(
			returnRouteRepository,
			returnRouteDayRepository,
			returnRouteStopRepository,
			sleepJetlagResultRepository,
			memberRepository,
			cityRepository,
			sleepTimeCalculator
		);
	}

	@Test
	void createReturnRouteThrowsSleepResultNotFoundWhenResultDoesNotExist() {
		Member member = member(MEMBER_ID);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(sleepJetlagResultRepository.findById(RESULT_ID)).thenReturn(Optional.empty());

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.createReturnRoute(MEMBER_ID, RESULT_ID))
			.satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SLEEP_RESULT_NOT_FOUND));
	}

	@Test
	void createReturnRouteThrowsSleepResultForbiddenWhenResultBelongsToOtherMember() {
		Member member = member(MEMBER_ID);
		SleepJetlagResult result = sleepJetlagResult(member(2L));
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(sleepJetlagResultRepository.findById(RESULT_ID)).thenReturn(Optional.of(result));

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.createReturnRoute(MEMBER_ID, RESULT_ID))
			.satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SLEEP_RESULT_FORBIDDEN));
	}

	@Test
	void createReturnRouteThrowsReturnRouteCityNotFoundWithDirectionAndGapMinutesWhenNoCandidateExists() {
		Member member = member(MEMBER_ID);
		SleepJetlagResult result = sleepJetlagResult(member);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(sleepJetlagResultRepository.findById(RESULT_ID)).thenReturn(Optional.of(result));
		when(returnRouteRepository.findAllByMemberAndStatus(member, ReturnRouteStatus.IN_PROGRESS))
			.thenReturn(List.of());
		when(sleepTimeCalculator.reached(any(), any(), any(), any())).thenReturn(false);
		when(sleepTimeCalculator.moveTowardTarget(LocalTime.of(3, 0), LocalTime.of(23, 0)))
			.thenReturn(LocalTime.of(2, 30));
		when(sleepTimeCalculator.moveTowardTarget(LocalTime.of(11, 0), LocalTime.of(7, 0)))
			.thenReturn(LocalTime.of(10, 30));
		when(sleepTimeCalculator.calculateMidTime(LocalTime.of(2, 30), LocalTime.of(10, 30)))
			.thenReturn(LocalTime.of(6, 0));
		when(sleepTimeCalculator.signedMidDifferenceMinutes(LocalTime.of(3, 0), LocalTime.of(6, 0)))
			.thenReturn(180);
		when(cityRepository.findAllByDirectionAndGapMinutes(CityDirection.WEST, 180)).thenReturn(List.of());

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.createReturnRoute(MEMBER_ID, RESULT_ID))
			.satisfies(exception -> {
				assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RETURN_ROUTE_CITY_NOT_FOUND);
				assertThat(exception.getMessage()).contains("direction=WEST", "gapMinutes=180");
			});
	}

	@Test
	void createReturnRouteMapsWest15MinutesToSeoulWithoutFindingCityCandidates() {
		Member member = member(MEMBER_ID);
		SleepJetlagResult result = sleepJetlagResult(member);
		City seoul = city(CityDirection.BASE);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(sleepJetlagResultRepository.findById(RESULT_ID)).thenReturn(Optional.of(result));
		when(returnRouteRepository.findAllByMemberAndStatus(member, ReturnRouteStatus.IN_PROGRESS))
			.thenReturn(List.of());
		when(returnRouteRepository.save(any(ReturnRoute.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(returnRouteDayRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(sleepTimeCalculator.reached(any(), any(), any(), any())).thenReturn(false, true);
		when(sleepTimeCalculator.moveTowardTarget(LocalTime.of(3, 0), LocalTime.of(23, 0)))
			.thenReturn(LocalTime.of(2, 45));
		when(sleepTimeCalculator.moveTowardTarget(LocalTime.of(11, 0), LocalTime.of(7, 0)))
			.thenReturn(LocalTime.of(10, 45));
		when(sleepTimeCalculator.calculateMidTime(LocalTime.of(2, 45), LocalTime.of(10, 45)))
			.thenReturn(LocalTime.of(6, 45));
		when(sleepTimeCalculator.signedMidDifferenceMinutes(LocalTime.of(3, 0), LocalTime.of(6, 45)))
			.thenReturn(15);
		when(cityRepository.findFirstByDirectionOrderByDisplayOrderAsc(CityDirection.BASE))
			.thenReturn(Optional.of(seoul));

		returnRouteService.createReturnRoute(MEMBER_ID, RESULT_ID);

		ArgumentCaptor<ReturnRouteDay> dayCaptor = ArgumentCaptor.forClass(ReturnRouteDay.class);
		verify(returnRouteDayRepository).save(dayCaptor.capture());
		assertThat(dayCaptor.getValue().getCheckpointCity()).isSameAs(seoul);
		verify(cityRepository, never()).findAllByDirectionAndGapMinutes(CityDirection.WEST, 15);
	}

	@Test
	void createReturnRouteThrowsSeoulCityNotFoundWhenBaseCityDoesNotExist() {
		Member member = member(MEMBER_ID);
		SleepJetlagResult result = sleepJetlagResult(member);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(sleepJetlagResultRepository.findById(RESULT_ID)).thenReturn(Optional.of(result));
		when(returnRouteRepository.findAllByMemberAndStatus(member, ReturnRouteStatus.IN_PROGRESS))
			.thenReturn(List.of());
		when(sleepTimeCalculator.reached(any(), any(), any(), any())).thenReturn(true);
		when(cityRepository.findFirstByDirectionOrderByDisplayOrderAsc(CityDirection.BASE))
			.thenReturn(Optional.empty());

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.createReturnRoute(MEMBER_ID, RESULT_ID))
			.satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEOUL_CITY_NOT_FOUND));
	}

	@Test
	void getCurrentRouteThrowsReturnRouteNotFoundWhenNoRouteIsInProgress() {
		Member member = member(MEMBER_ID);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(returnRouteRepository.findFirstByMemberAndStatusOrderByCreatedAtDesc(member,
			ReturnRouteStatus.IN_PROGRESS)).thenReturn(Optional.empty());

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.getCurrentRoute(MEMBER_ID))
			.satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RETURN_ROUTE_NOT_FOUND));
	}

	@Test
	void getCurrentBoardingPassThrowsReturnRouteDayNotFoundWhenCurrentDayDoesNotExist() {
		Member member = member(MEMBER_ID);
		ReturnRoute route = ReturnRoute.create(member, sleepJetlagResult(member), 30, 3);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		when(returnRouteRepository.findFirstByMemberAndStatusOrderByCreatedAtDesc(member,
			ReturnRouteStatus.IN_PROGRESS)).thenReturn(Optional.of(route));
		when(returnRouteDayRepository.findByReturnRouteAndDayNumber(route, 1)).thenReturn(Optional.empty());

		assertThatExceptionOfType(BusinessException.class)
			.isThrownBy(() -> returnRouteService.getCurrentBoardingPass(MEMBER_ID))
			.satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RETURN_ROUTE_DAY_NOT_FOUND));
	}

	private Member member(Long id) {
		Member member = Member.create("member" + id, "password", "nickname" + id);
		ReflectionTestUtils.setField(member, "id", id);
		return member;
	}

	private SleepJetlagResult sleepJetlagResult(Member member) {
		SleepRecord sleepRecord = SleepRecord.create(
			member,
			LocalTime.of(3, 0),
			LocalTime.of(11, 0),
			480,
			LocalTime.of(23, 0),
			LocalTime.of(7, 0),
			480
		);
		return SleepJetlagResult.create(
			sleepRecord,
			member,
			LocalTime.of(7, 0),
			LocalTime.of(3, 0),
			240,
			JetlagDirection.WEST,
			city(CityDirection.WEST),
			LocalDate.of(2026, 7, 11)
		);
	}

	private City city(CityDirection direction) {
		return City.create(
			"대한민국",
			"서울",
			"SEOUL",
			"ICN",
			"+09:00",
			"Asia/Seoul",
			direction,
			0,
			0,
			30,
			false,
			1,
			37.5665,
			126.9780
		);
	}
}
