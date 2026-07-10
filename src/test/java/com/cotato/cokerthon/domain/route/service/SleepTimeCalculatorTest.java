package com.cotato.cokerthon.domain.route.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class SleepTimeCalculatorTest {

	private final SleepTimeCalculator sleepTimeCalculator = new SleepTimeCalculator();

	@Test
	void movesEachTimeByAtMostThirtyMinutesTowardTarget() {
		LocalTime result = sleepTimeCalculator.moveTowardTarget(LocalTime.of(3, 0), LocalTime.of(1, 0));

		assertThat(result).isEqualTo(LocalTime.of(2, 30));
	}

	@Test
	void keepsTimeWhenTargetAlreadyReached() {
		LocalTime result = sleepTimeCalculator.moveTowardTarget(LocalTime.of(7, 0), LocalTime.of(7, 0));

		assertThat(result).isEqualTo(LocalTime.of(7, 0));
	}

	@Test
	void calculatesMidTimeAcrossMidnight() {
		LocalTime result = sleepTimeCalculator.calculateMidTime(LocalTime.of(23, 0), LocalTime.of(7, 0));

		assertThat(result).isEqualTo(LocalTime.of(3, 0));
	}

	@Test
	void calculatesSignedMidDifferenceForWestCandidate() {
		int result = sleepTimeCalculator.signedMidDifferenceMinutes(LocalTime.of(5, 0), LocalTime.of(6, 0));

		assertThat(result).isEqualTo(60);
	}
}
