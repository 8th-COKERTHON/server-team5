package com.cotato.cokerthon.domain.route.repository;

import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteDay;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRouteDayRepository extends JpaRepository<ReturnRouteDay, Long> {

	Optional<ReturnRouteDay> findByReturnRouteAndDayNumber(ReturnRoute returnRoute, int dayNumber);

	List<ReturnRouteDay> findAllByReturnRouteOrderByDayNumberAsc(ReturnRoute returnRoute);
}
