package com.cotato.cokerthon.domain.member.service;

import com.cotato.cokerthon.domain.city.entity.City;
import com.cotato.cokerthon.domain.city.entity.CityDirection;
import com.cotato.cokerthon.domain.city.repository.CityRepository;
import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.sleep.entity.SleepJetlagResult;
import com.cotato.cokerthon.domain.sleep.repository.SleepJetlagResultRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final SleepJetlagResultRepository sleepJetlagResultRepository;
	private final CityRepository cityRepository;

	public MemberService(MemberRepository memberRepository,
		SleepJetlagResultRepository sleepJetlagResultRepository,
		CityRepository cityRepository) {
		this.memberRepository = memberRepository;
		this.sleepJetlagResultRepository = sleepJetlagResultRepository;
		this.cityRepository = cityRepository;
	}

	public MemberResponse getMyInfo(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		SleepJetlagResult latestResult = sleepJetlagResultRepository
			.findFirstByMemberOrderByCreatedAtDesc(member)
			.orElse(null);

		return MemberResponse.from(member, latestResult, getSeoul());
	}

	private City getSeoul() {
		return cityRepository.findByDirection(CityDirection.BASE)
			.orElseThrow(() -> new BusinessException(ErrorCode.CITY_NOT_MATCHED));
	}
}
