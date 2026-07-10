package com.cotato.cokerthon.domain.route.entity;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// 귀국 루트. 특정 SleepJetlagResult(현재 위치 결과)를 기준으로 생성
@Entity
@Table(name = "return_routes")
public class ReturnRoute extends BaseCreatedAtEntity {

	// 회원
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	// 기준이 된 현재 위치 결과
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id", nullable = false)
	private SleepJetlagResult result;

	// 하루 조정 분 ("DAILY ADJUST")
	@Column(nullable = false)
	private int dailyAdjustMinutes;

	// 총 소요일 ("DURATION", 계산값)
	@Column(nullable = false)
	private int durationDays;

	protected ReturnRoute() {
	}

	private ReturnRoute(Member member, SleepJetlagResult result, int dailyAdjustMinutes, int durationDays) {
		this.member = member;
		this.result = result;
		this.dailyAdjustMinutes = dailyAdjustMinutes;
		this.durationDays = durationDays;
	}

	public static ReturnRoute create(Member member, SleepJetlagResult result, int dailyAdjustMinutes,
		int durationDays) {
		return new ReturnRoute(member, result, dailyAdjustMinutes, durationDays);
	}

	public Member getMember() {
		return member;
	}

	public SleepJetlagResult getResult() {
		return result;
	}

	public int getDailyAdjustMinutes() {
		return dailyAdjustMinutes;
	}

	public int getDurationDays() {
		return durationDays;
	}
}
