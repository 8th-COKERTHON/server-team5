package com.cotato.cokerthon.domain.companion.repository;

import com.cotato.cokerthon.domain.companion.entity.Companion;
import com.cotato.cokerthon.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanionRepository extends JpaRepository<Companion, Long> {

	boolean existsByMemberAndCompanionMember(Member member, Member companionMember);

	Optional<Companion> findByMemberAndCompanionMember(Member member, Member companionMember);

	List<Companion> findAllByMember(Member member);
}
