package com.cotato.cokerthon.domain.companion.service;

import com.cotato.cokerthon.domain.companion.dto.request.CompanionCreateRequest;
import com.cotato.cokerthon.domain.companion.dto.response.CompanionResponse;
import com.cotato.cokerthon.domain.companion.dto.response.CompanionSearchResponse;
import com.cotato.cokerthon.domain.companion.entity.Companion;
import com.cotato.cokerthon.domain.companion.repository.CompanionRepository;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.repository.SleepJetlagResultRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CompanionService {

	private final CompanionRepository companionRepository;
	private final MemberRepository memberRepository;
	private final SleepJetlagResultRepository sleepJetlagResultRepository;

	public CompanionService(
		CompanionRepository companionRepository,
		MemberRepository memberRepository,
		SleepJetlagResultRepository sleepJetlagResultRepository
	) {
		this.companionRepository = companionRepository;
		this.memberRepository = memberRepository;
		this.sleepJetlagResultRepository = sleepJetlagResultRepository;
	}

	public CompanionSearchResponse search(Long memberId, String targetLoginId) {
		Member me = getMember(memberId);
		Member target = getMemberByLoginId(targetLoginId);

		boolean alreadyCompanion = companionRepository.existsByMemberAndCompanionMember(me, target);
		return CompanionSearchResponse.of(target, alreadyCompanion);
	}

	@Transactional
	public CompanionResponse add(Long memberId, CompanionCreateRequest request) {
		Member me = getMember(memberId);
		Member target = getMemberByLoginId(request.loginId());

		if (me.getId().equals(target.getId())) {
			throw new BusinessException(ErrorCode.SELF_COMPANION_NOT_ALLOWED);
		}
		if (companionRepository.existsByMemberAndCompanionMember(me, target)) {
			throw new BusinessException(ErrorCode.ALREADY_COMPANION);
		}

		companionRepository.save(Companion.create(me, target));
		return toCompanionResponse(target);
	}

	public List<CompanionResponse> getCompanions(Long memberId) {
		Member me = getMember(memberId);

		return companionRepository.findAllByMember(me).stream()
			.map(companion -> toCompanionResponse(companion.getCompanionMember()))
			.toList();
	}

	@Transactional
	public void remove(Long memberId, Long companionMemberId) {
		Member me = getMember(memberId);
		Member target = getMember(companionMemberId);

		Companion companion = companionRepository.findByMemberAndCompanionMember(me, target)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		companionRepository.delete(companion);
	}

	private CompanionResponse toCompanionResponse(Member companionMember) {
		SleepJetlagResult latestResult = sleepJetlagResultRepository
			.findFirstByMemberOrderByCreatedAtDesc(companionMember)
			.orElse(null);

		return CompanionResponse.of(companionMember, latestResult);
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
	}

	private Member getMemberByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
	}
}
