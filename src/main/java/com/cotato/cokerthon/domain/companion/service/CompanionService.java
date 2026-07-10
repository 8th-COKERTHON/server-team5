package com.cotato.cokerthon.domain.companion.service;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
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
	private final CityRepository cityRepository;

	public CompanionService(
		CompanionRepository companionRepository,
		MemberRepository memberRepository,
		SleepJetlagResultRepository sleepJetlagResultRepository,
		CityRepository cityRepository
	) {
		this.companionRepository = companionRepository;
		this.memberRepository = memberRepository;
		this.sleepJetlagResultRepository = sleepJetlagResultRepository;
		this.cityRepository = cityRepository;
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
		return toCompanionResponse(target, getSeoul());
	}

	public List<CompanionResponse> getCompanions(Long memberId) {
		Member me = getMember(memberId);
		City seoul = getSeoul();

		return companionRepository.findAllByMember(me).stream()
			.map(companion -> toCompanionResponse(companion.getCompanionMember(), seoul))
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

	private CompanionResponse toCompanionResponse(Member companionMember, City seoul) {
		SleepJetlagResult latestResult = sleepJetlagResultRepository
			.findFirstByMemberOrderByCreatedAtDesc(companionMember)
			.orElse(null);

		return CompanionResponse.of(companionMember, latestResult, seoul);
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
	}

	private Member getMemberByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
	}

	private City getSeoul() {
		return cityRepository.findByDirection(CityDirection.BASE)
			.orElseThrow(() -> new BusinessException(ErrorCode.CITY_NOT_MATCHED));
	}
}
