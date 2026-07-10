package com.cotato.cokerthon.domain.route.repository;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.route.entity.ReturnRoute;
import com.cotato.cokerthon.domain.route.entity.ReturnRouteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRouteRepository extends JpaRepository<ReturnRoute, Long> {

	List<ReturnRoute> findAllByMemberAndStatus(Member member, ReturnRouteStatus status);

	Optional<ReturnRoute> findFirstByMemberAndStatusOrderByCreatedAtDesc(Member member, ReturnRouteStatus status);
}
