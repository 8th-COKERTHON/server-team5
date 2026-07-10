package com.cotato.cokerthon.domain.sleep.entity;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

// 수면시차 계산 결과 ("오늘의 항공권"). SleepRecord 1건당 결과 1건(1:1)
@Entity
@Table(name = "sleep_jetlag_results")
public class SleepJetlagResult extends BaseCreatedAtEntity {

	// 원본 입력 (1:1, unique)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sleep_record_id", nullable = false, unique = true)
	private SleepRecord sleepRecord;

	// 결과를 소유한 회원
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	// 현재 수면 중심시각
	@Column(nullable = false)
	private LocalTime currentMidTime;

	// 목표 수면 중심시각
	@Column(nullable = false)
	private LocalTime targetMidTime;

	// 수면시차 절대값(분)
	@Column(nullable = false)
	private int jetlagMinutes;

	// 서울 대비 조정 방향
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private JetlagDirection direction;

	// 시차 매핑된 대표 도시
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matched_city_id", nullable = false)
	private City matchedCity;

	// 결과 조회일
	@Column(nullable = false)
	private LocalDate resultDate;

	protected SleepJetlagResult() {
	}

	private SleepJetlagResult(SleepRecord sleepRecord, Member member, LocalTime currentMidTime,
		LocalTime targetMidTime, int jetlagMinutes, JetlagDirection direction, City matchedCity,
		LocalDate resultDate) {
		this.sleepRecord = sleepRecord;
		this.member = member;
		this.currentMidTime = currentMidTime;
		this.targetMidTime = targetMidTime;
		this.jetlagMinutes = jetlagMinutes;
		this.direction = direction;
		this.matchedCity = matchedCity;
		this.resultDate = resultDate;
	}

	public static SleepJetlagResult create(SleepRecord sleepRecord, Member member, LocalTime currentMidTime,
		LocalTime targetMidTime, int jetlagMinutes, JetlagDirection direction, City matchedCity,
		LocalDate resultDate) {
		return new SleepJetlagResult(sleepRecord, member, currentMidTime, targetMidTime, jetlagMinutes,
			direction, matchedCity, resultDate);
	}

	public SleepRecord getSleepRecord() {
		return sleepRecord;
	}

	public Member getMember() {
		return member;
	}

	public LocalTime getCurrentMidTime() {
		return currentMidTime;
	}

	public LocalTime getTargetMidTime() {
		return targetMidTime;
	}

	public int getJetlagMinutes() {
		return jetlagMinutes;
	}

	public JetlagDirection getDirection() {
		return direction;
	}

	public City getMatchedCity() {
		return matchedCity;
	}

	public LocalDate getResultDate() {
		return resultDate;
	}
}
