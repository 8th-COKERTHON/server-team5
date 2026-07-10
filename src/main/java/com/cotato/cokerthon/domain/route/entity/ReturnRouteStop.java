package com.cotato.cokerthon.domain.route.entity;

import com.cotato.cokerthon.domain.city.entity.City;
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

// 귀국 경유지 요약 ("전체 귀국 루트" 화면). 시드성 요약 데이터라 생성 이력 불필요
@Entity
@Table(
	name = "return_route_stops",
	uniqueConstraints = @UniqueConstraint(columnNames = {"return_route_id", "stop_order"})
)
public class ReturnRouteStop {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 소속 루트
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "return_route_id", nullable = false)
	private ReturnRoute returnRoute;

	// 경유 도시
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	// 서울과 가까워지는 순서 (0=현재 위치, 마지막=서울)
	@Column(nullable = false)
	private int stopOrder;

	protected ReturnRouteStop() {
	}

	private ReturnRouteStop(ReturnRoute returnRoute, City city, int stopOrder) {
		this.returnRoute = returnRoute;
		this.city = city;
		this.stopOrder = stopOrder;
	}

	public static ReturnRouteStop create(ReturnRoute returnRoute, City city, int stopOrder) {
		return new ReturnRouteStop(returnRoute, city, stopOrder);
	}

	public Long getId() {
		return id;
	}

	public ReturnRoute getReturnRoute() {
		return returnRoute;
	}

	public City getCity() {
		return city;
	}

	public int getStopOrder() {
		return stopOrder;
	}
}
