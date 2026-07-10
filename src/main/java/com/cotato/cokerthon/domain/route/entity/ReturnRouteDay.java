package com.cotato.cokerthon.domain.route.entity;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalTime;

// 일별 조정 스케줄 ("오늘 귀국 루트" / "오늘의 비행 가이드" / "BOARDING START" 화면)
@Entity
@Table(
	name = "return_route_days",
	uniqueConstraints = @UniqueConstraint(columnNames = {"return_route_id", "day_number"})
)
public class ReturnRouteDay extends BaseCreatedAtEntity {

	// 소속 루트
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "return_route_id", nullable = false)
	private ReturnRoute returnRoute;

	// 몇 일차 (1부터 durationDays까지)
	@Column(nullable = false)
	private int dayNumber;

	// 해당 일자의 목적지 체크포인트 도시
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checkpoint_city_id", nullable = false)
	private City checkpointCity;

	// 그날 실제(전날 목표) 취침 시각
	@Column(nullable = false)
	private LocalTime currentBedtime;

	// 그날 실제(전날 목표) 기상 시각
	@Column(nullable = false)
	private LocalTime currentWaketime;

	// 그날 목표 취침 시각 (daily-adjust만큼 당김)
	@Column(nullable = false)
	private LocalTime targetBedtime;

	// 그날 목표 기상 시각 (daily-adjust만큼 당김)
	@Column(nullable = false)
	private LocalTime targetWaketime;

	// 카페인 마감 시각 (추정: 목표취침 - 8h)
	private LocalTime caffeineCutoffTime;

	// 취침 준비 시작 시각 (추정: 목표취침 - 1h)
	private LocalTime sleepPrepTime;

	// 취침 가능 시작 시각 (추정: 목표취침 - 15분)
	private LocalTime bedtimeWindowStart;

	// 취침 가능 종료 시각 (추정: 목표취침 + 15분)
	private LocalTime bedtimeWindowEnd;

	protected ReturnRouteDay() {
	}

	private ReturnRouteDay(ReturnRoute returnRoute, int dayNumber, City checkpointCity,
		LocalTime currentBedtime, LocalTime currentWaketime, LocalTime targetBedtime, LocalTime targetWaketime,
		LocalTime caffeineCutoffTime, LocalTime sleepPrepTime, LocalTime bedtimeWindowStart,
		LocalTime bedtimeWindowEnd) {
		this.returnRoute = returnRoute;
		this.dayNumber = dayNumber;
		this.checkpointCity = checkpointCity;
		this.currentBedtime = currentBedtime;
		this.currentWaketime = currentWaketime;
		this.targetBedtime = targetBedtime;
		this.targetWaketime = targetWaketime;
		this.caffeineCutoffTime = caffeineCutoffTime;
		this.sleepPrepTime = sleepPrepTime;
		this.bedtimeWindowStart = bedtimeWindowStart;
		this.bedtimeWindowEnd = bedtimeWindowEnd;
	}

	public static ReturnRouteDay create(ReturnRoute returnRoute, int dayNumber, City checkpointCity,
		LocalTime currentBedtime, LocalTime currentWaketime, LocalTime targetBedtime, LocalTime targetWaketime,
		LocalTime caffeineCutoffTime, LocalTime sleepPrepTime, LocalTime bedtimeWindowStart,
		LocalTime bedtimeWindowEnd) {
		return new ReturnRouteDay(returnRoute, dayNumber, checkpointCity, currentBedtime, currentWaketime,
			targetBedtime, targetWaketime, caffeineCutoffTime, sleepPrepTime, bedtimeWindowStart,
			bedtimeWindowEnd);
	}

	public ReturnRoute getReturnRoute() {
		return returnRoute;
	}

	public int getDayNumber() {
		return dayNumber;
	}

	public City getCheckpointCity() {
		return checkpointCity;
	}

	public LocalTime getCurrentBedtime() {
		return currentBedtime;
	}

	public LocalTime getCurrentWaketime() {
		return currentWaketime;
	}

	public LocalTime getTargetBedtime() {
		return targetBedtime;
	}

	public LocalTime getTargetWaketime() {
		return targetWaketime;
	}

	public LocalTime getCaffeineCutoffTime() {
		return caffeineCutoffTime;
	}

	public LocalTime getSleepPrepTime() {
		return sleepPrepTime;
	}

	public LocalTime getBedtimeWindowStart() {
		return bedtimeWindowStart;
	}

	public LocalTime getBedtimeWindowEnd() {
		return bedtimeWindowEnd;
	}
}
