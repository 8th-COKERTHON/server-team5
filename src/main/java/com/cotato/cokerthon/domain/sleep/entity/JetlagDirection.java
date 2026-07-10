package com.cotato.cokerthon.domain.sleep.entity;

// 서울(현재 생활권) 대비 목표 수면시간 조정 방향
public enum JetlagDirection {
	WEST, // 취침/기상을 늦춰야 함
	EAST, // 취침/기상을 당겨야 함
	SAME  // 시차 없음
}
