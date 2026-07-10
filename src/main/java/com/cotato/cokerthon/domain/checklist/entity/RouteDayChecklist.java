package com.cotato.cokerthon.domain.checklist.entity;

import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

// 일자별 체크리스트 체크 여부. ReturnRouteDay 생성 시 ChecklistItem 4건이 함께 자동 생성되는 것을 가정
@Entity
@Table(
	name = "route_day_checklists",
	uniqueConstraints = @UniqueConstraint(columnNames = {"return_route_day_id", "checklist_item_id"})
)
public class RouteDayChecklist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 소속 일자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "return_route_day_id", nullable = false)
	private ReturnRouteDay returnRouteDay;

	// 체크리스트 항목
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checklist_item_id", nullable = false)
	private ChecklistItem checklistItem;

	// 체크 여부
	@Column(name = "is_checked", nullable = false)
	private boolean checked;

	// 체크일시
	private LocalDateTime checkedAt;

	protected RouteDayChecklist() {
	}

	private RouteDayChecklist(ReturnRouteDay returnRouteDay, ChecklistItem checklistItem) {
		this.returnRouteDay = returnRouteDay;
		this.checklistItem = checklistItem;
		this.checked = false;
	}

	public static RouteDayChecklist create(ReturnRouteDay returnRouteDay, ChecklistItem checklistItem) {
		return new RouteDayChecklist(returnRouteDay, checklistItem);
	}

	// 체크리스트 항목을 체크 완료로 표시
	public void check() {
		this.checked = true;
		this.checkedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public ReturnRouteDay getReturnRouteDay() {
		return returnRouteDay;
	}

	public ChecklistItem getChecklistItem() {
		return checklistItem;
	}

	public boolean isChecked() {
		return checked;
	}

	public LocalDateTime getCheckedAt() {
		return checkedAt;
	}
}
