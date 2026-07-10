package com.cotato.cokerthon.domain.route.repository;

import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRouteStopRepository extends JpaRepository<ReturnRouteStop, Long> {

	List<ReturnRouteStop> findAllByReturnRouteOrderByStopOrderAsc(ReturnRoute returnRoute);
}
