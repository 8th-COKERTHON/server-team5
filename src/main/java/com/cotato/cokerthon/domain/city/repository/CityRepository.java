package com.cotato.cokerthon.domain.city.repository;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CityRepository extends JpaRepository<City, Integer> {

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

	Optional<City> findFirstByDirectionOrderByDisplayOrderAsc(CityDirection direction);
}
