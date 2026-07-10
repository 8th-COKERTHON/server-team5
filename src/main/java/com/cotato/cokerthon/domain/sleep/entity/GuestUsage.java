package com.cotato.cokerthon.domain.sleep.entity;

import com.cotato.cokerthon.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// 비회원 수면시차 1회 체험 사용 이력. 같은 device_id로는 두 번째 계산을 막는 용도
@Entity
@Table(name = "guest_usages")
public class GuestUsage extends BaseCreatedAtEntity {

	// 클라이언트가 생성해 보관하는 기기 식별자 (예: 앱 최초 실행 시 만든 UUID)
	@Column(nullable = false, unique = true, length = 100)
	private String deviceId;

	protected GuestUsage() {
	}

	private GuestUsage(String deviceId) {
		this.deviceId = deviceId;
	}

	public static GuestUsage create(String deviceId) {
		return new GuestUsage(deviceId);
	}

	public String getDeviceId() {
		return deviceId;
	}
}
