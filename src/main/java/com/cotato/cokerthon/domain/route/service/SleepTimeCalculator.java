package com.cotato.cokerthon.domain.route.service;

import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class SleepTimeCalculator {

	private static final int DAY_MINUTES = 24 * 60;
	private static final int HALF_DAY_MINUTES = 12 * 60;
	private static final int MAX_DAILY_MOVE_MINUTES = 30;

	public LocalTime moveTowardTarget(LocalTime current, LocalTime target) {
		int difference = signedDifferenceMinutes(current, target);
		if (difference == 0) {
			return current;
		}

		int moveMinutes = Math.min(MAX_DAILY_MOVE_MINUTES, Math.abs(difference));
		return current.plusMinutes(difference > 0 ? moveMinutes : -moveMinutes);
	}

	public LocalTime calculateMidTime(LocalTime bedtime, LocalTime waketime) {
		int bedtimeMinutes = toMinutes(bedtime);
		int waketimeMinutes = toMinutes(waketime);
		if (waketimeMinutes <= bedtimeMinutes) {
			waketimeMinutes += DAY_MINUTES;
		}

		return fromMinutes(bedtimeMinutes + ((waketimeMinutes - bedtimeMinutes) / 2));
	}

	public int signedMidDifferenceMinutes(LocalTime targetMidTime, LocalTime currentMidTime) {
		return signedDifferenceMinutes(targetMidTime, currentMidTime);
	}

	public boolean reached(LocalTime currentBedtime, LocalTime currentWaketime,
		LocalTime targetBedtime, LocalTime targetWaketime) {
		return currentBedtime.equals(targetBedtime) && currentWaketime.equals(targetWaketime);
	}

	private int signedDifferenceMinutes(LocalTime from, LocalTime to) {
		int difference = toMinutes(to) - toMinutes(from);
		if (difference > HALF_DAY_MINUTES) {
			difference -= DAY_MINUTES;
		}
		if (difference < -HALF_DAY_MINUTES) {
			difference += DAY_MINUTES;
		}

		return difference;
	}

	private int toMinutes(LocalTime time) {
		return (time.getHour() * 60) + time.getMinute();
	}

	private LocalTime fromMinutes(int minutes) {
		int normalized = ((minutes % DAY_MINUTES) + DAY_MINUTES) % DAY_MINUTES;
		return LocalTime.of(normalized / 60, normalized % 60);
	}
}
