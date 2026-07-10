package com.cotato.cokerthon.domain.sleep.repository;

import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepJetlagResultRepository extends JpaRepository<SleepJetlagResult, Long> {

	// 동행자 카드에 보여줄 "마지막 기록" 조회용
	Optional<SleepJetlagResult> findFirstByMemberOrderByCreatedAtDesc(Member member);
}
