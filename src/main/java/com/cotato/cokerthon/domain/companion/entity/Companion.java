package com.cotato.cokerthon.domain.companion.entity;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// 동행자 관계. 상호 동의 없는 단방향(추가 버튼 = 즉시 등록)
@Entity
@Table(
	name = "companions",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "companion_member_id"})
)
public class Companion extends BaseCreatedAtEntity {

	// 등록한 사람 (나)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	// 추가된 동행자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companion_member_id", nullable = false)
	private Member companionMember;

	protected Companion() {
	}

	private Companion(Member member, Member companionMember) {
		this.member = member;
		this.companionMember = companionMember;
	}

	public static Companion create(Member member, Member companionMember) {
		return new Companion(member, companionMember);
	}

	public Member getMember() {
		return member;
	}

	public Member getCompanionMember() {
		return companionMember;
	}
}
