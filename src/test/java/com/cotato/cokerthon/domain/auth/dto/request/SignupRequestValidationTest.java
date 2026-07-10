package com.cotato.cokerthon.domain.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignupRequestValidationTest {

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void setUp() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		validatorFactory.close();
	}

	@Test
	void 회원가입_요청은_화면_제약을_만족하면_유효하다() {
		SignupRequest request = new SignupRequest("sleepair123", "password1!", "채은A1");

		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

		assertThat(violations).isEmpty();
	}

	@Test
	void 닉네임은_필수이며_한글_영문_숫자_10자_이하여야_한다() {
		assertThat(validate(new SignupRequest("sleepair123", "password1!", ""))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "password1!", "abcdefghijk"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "password1!", "채은!"))).isNotEmpty();
	}

	@Test
	void 아이디는_필수이며_소문자_영문과_숫자_4자_이상_20자_이하여야_한다() {
		assertThat(validate(new SignupRequest("", "password1!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("abc", "password1!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("abcdefghijklmnopqrstu", "password1!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("Sleepair123", "password1!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleep_air", "password1!", "채은"))).isNotEmpty();
	}

	@Test
	void 비밀번호는_필수이며_영문_숫자_특수문자를_포함한_8자_이상_20자_이하여야_한다() {
		assertThat(validate(new SignupRequest("sleepair123", "", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "pass1!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "passwordpassword123!!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "password!", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "password1", "채은"))).isNotEmpty();
		assertThat(validate(new SignupRequest("sleepair123", "12345678!", "채은"))).isNotEmpty();
	}

	private Set<ConstraintViolation<SignupRequest>> validate(SignupRequest request) {
		return validator.validate(request);
	}
}
