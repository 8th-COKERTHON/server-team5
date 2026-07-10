package com.cotato.cokerthon.domain.checklist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 탑승 전 체크리스트 마스터 (확정 4개: 조명 낮추기 / 씻기 / 휴대폰 충전하기 / 알람 확인하기). 시드 데이터
@Entity
@Table(name = "checklist_items")
public class ChecklistItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 항목명
	@Column(nullable = false, length = 50)
	private String title;

	// 노출 순서
	@Column(nullable = false)
	private int displayOrder;

	protected ChecklistItem() {
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}
}
