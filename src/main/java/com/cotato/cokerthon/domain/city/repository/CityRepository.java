package com.cotato.cokerthon.domain.city.repository;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {

	List<City> findByDirectionAndGapMinutes(CityDirection direction, int gapMinutes);

	List<City> findByDirectionAndMappingMinMinutesLessThanEqualAndMappingMaxMinutesGreaterThanEqual(
		CityDirection direction,
		int minMinutes,
		int maxMinutes
	);

	List<City> findByDirectionAndMappingMinMinutesLessThanEqualAndMappingMaxMinutesIsNull(
		CityDirection direction,
		int minMinutes
	);

	Optional<City> findFirstByDirectionOrderByDisplayOrderAsc(CityDirection direction);
}
