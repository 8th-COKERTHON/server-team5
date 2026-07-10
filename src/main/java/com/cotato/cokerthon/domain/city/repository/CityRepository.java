package com.cotato.cokerthon.domain.city.repository;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CityRepository extends JpaRepository<City, Integer> {

	// BASE(서울)는 유일하므로 출발지 조회용으로 사용
	Optional<City> findByDirection(CityDirection direction);

	// 같은 시차 구간에 여러 도시가 매핑될 수 있어 전부 반환 (호출부에서 랜덤 선택)
	@Query("""
		select c from City c
		where c.direction = :direction
			and c.mappingMinMinutes <= :gapMinutes
			and (c.mappingMaxMinutes is null or c.mappingMaxMinutes > :gapMinutes)
		""")
	List<City> findAllByDirectionAndGapMinutes(
		@Param("direction") CityDirection direction,
		@Param("gapMinutes") int gapMinutes
	);
}
