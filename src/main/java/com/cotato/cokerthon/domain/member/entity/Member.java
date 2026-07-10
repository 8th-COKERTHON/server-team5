package com.cotato.cokerthon.domain.member.entity;

import com.cotato.cokerthon.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member extends BaseEntity {

	@Column(nullable = false, unique = true, length = 20)
	private String loginId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 30)
	private String nickname;

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
}
