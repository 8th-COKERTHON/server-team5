package com.cotato.cokerthon.domain.auth.service;

import com.cotato.cokerthon.domain.auth.dto.request.LoginRequest;
import com.cotato.cokerthon.domain.auth.dto.request.SignupRequest;
import com.cotato.cokerthon.domain.auth.dto.response.TokenResponse;
import com.cotato.cokerthon.domain.member.entity.Member;
import com.cotato.cokerthon.domain.member.repository.MemberRepository;
import com.cotato.cokerthon.domain.member.dto.response.MemberResponse;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import com.cotato.cokerthon.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthService(
		MemberRepository memberRepository,
		PasswordEncoder passwordEncoder,
		JwtTokenProvider jwtTokenProvider
	) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Transactional
	public MemberResponse signup(SignupRequest request) {
		if (memberRepository.existsByLoginId(request.id())) {
			throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
		}

		Member member = Member.create(
			request.id(),
			passwordEncoder.encode(request.password()),
			request.nickname()
		);

		return MemberResponse.from(memberRepository.save(member), null);
	}

	public TokenResponse login(LoginRequest request) {
		Member member = memberRepository.findByLoginId(request.id())
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

		if (!passwordEncoder.matches(request.password(), member.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
		}

		return TokenResponse.bearer(jwtTokenProvider.createAccessToken(member.getId()));
	}
}
