package com.cotato.cokerthon.domain.member.entity;

import com.cotato.cokerthon.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
public class Member extends BaseEntity {

	// 로그인 아이디
	@Column(nullable = false, unique = true, length = 20)
	private String loginId;

	// 암호화된 비밀번호
	@Column(nullable = false)
	private String password;

	// 닉네임 (동행자 카드 등에 노출)
	@Column(nullable = false, length = 30)
	private String nickname;

	// 동행자 검색용 아이디 (예: meangg). 가입 시점엔 미수집, 동행자 기능 연동 시 채워짐
	@Column(unique = true, length = 30)
	private String handle;

	// 프로필 이미지 URL
	@Column(length = 500)
	private String profileImageUrl;

	// 이메일
	@Column(length = 255)
	private String email;

	// 탈퇴일시 (soft delete, null이면 활성 회원)
	private LocalDateTime withdrawnAt;

	protected Member() {
	}

	private Member(String loginId, String password, String nickname) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
	}

	public static Member create(String loginId, String encodedPassword, String nickname) {
		return new Member(loginId, encodedPassword, nickname);
	}

	public String getLoginId() {
		return loginId;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public String getHandle() {
		return handle;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getEmail() {
		return email;
	}

	public LocalDateTime getWithdrawnAt() {
		return withdrawnAt;
	}
}
