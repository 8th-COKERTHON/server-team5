package com.cotato.cokerthon.domain.sleep.entity;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

// 사용자가 입력한 현재/목표 수면 정보. 입력 1건은 이후 수정되지 않음(재계산 시 새 레코드 생성)
@Entity
@Table(name = "sleep_records")
public class SleepRecord extends BaseCreatedAtEntity {

	// 입력한 회원 (게스트는 서버에 저장하지 않음)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	// 현재 잠드는 시간
	@Column(nullable = false)
	private LocalTime currentBedtime;

	// 현재 기상 시간
	@Column(nullable = false)
	private LocalTime currentWaketime;

	// 현재 총 수면시간(분)
	@Column(nullable = false)
	private int currentSleepMinutes;

	// 목표 잠드는 시간
	@Column(nullable = false)
	private LocalTime targetBedtime;

	// 목표 기상 시간
	@Column(nullable = false)
	private LocalTime targetWaketime;

	// 목표 총 수면시간(분)
	@Column(nullable = false)
	private int targetSleepMinutes;

	protected SleepRecord() {
	}

	private SleepRecord(Member member, LocalTime currentBedtime, LocalTime currentWaketime,
		int currentSleepMinutes, LocalTime targetBedtime, LocalTime targetWaketime, int targetSleepMinutes) {
		this.member = member;
		this.currentBedtime = currentBedtime;
		this.currentWaketime = currentWaketime;
		this.currentSleepMinutes = currentSleepMinutes;
		this.targetBedtime = targetBedtime;
		this.targetWaketime = targetWaketime;
		this.targetSleepMinutes = targetSleepMinutes;
	}

	public static SleepRecord create(Member member, LocalTime currentBedtime, LocalTime currentWaketime,
		int currentSleepMinutes, LocalTime targetBedtime, LocalTime targetWaketime, int targetSleepMinutes) {
		return new SleepRecord(member, currentBedtime, currentWaketime, currentSleepMinutes,
			targetBedtime, targetWaketime, targetSleepMinutes);
	}

	public Member getMember() {
		return member;
	}

	public LocalTime getCurrentBedtime() {
		return currentBedtime;
	}

	public LocalTime getCurrentWaketime() {
		return currentWaketime;
	}

	public int getCurrentSleepMinutes() {
		return currentSleepMinutes;
	}

	public LocalTime getTargetBedtime() {
		return targetBedtime;
	}

	public LocalTime getTargetWaketime() {
		return targetWaketime;
	}

	public int getTargetSleepMinutes() {
		return targetSleepMinutes;
	}
}
