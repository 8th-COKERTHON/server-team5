package com.cotato.cokerthon.domain.city.entity;

// 서울 기준 시차 방향
public enum CityDirection {
	WEST, // 서쪽 (시간을 늦춰야 함)
	EAST, // 동쪽 (시간을 당겨야 함)
	BASE  // 서울과 동일 시간대
}
