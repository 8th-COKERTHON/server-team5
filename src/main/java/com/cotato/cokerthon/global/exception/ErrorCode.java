package com.cotato.cokerthon.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."),
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
	DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER_409_001", "이미 사용 중인 아이디입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
	SLEEP_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "SLEEP_RESULT_NOT_FOUND", "수면시차 계산 결과를 찾을 수 없습니다."),
	SLEEP_RESULT_FORBIDDEN(HttpStatus.FORBIDDEN, "SLEEP_RESULT_FORBIDDEN", "현재 로그인한 사용자의 수면시차 계산 결과가 아닙니다."),
	RETURN_ROUTE_CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RETURN_ROUTE_CITY_NOT_FOUND", "귀국 루트 경유 도시 후보를 찾을 수 없습니다."),
	SEOUL_CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "SEOUL_CITY_NOT_FOUND", "서울(BASE) 도시 데이터를 찾을 수 없습니다."),
	RETURN_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "RETURN_ROUTE_NOT_FOUND", "진행 중인 귀국 루트를 찾을 수 없습니다."),
	RETURN_ROUTE_DAY_NOT_FOUND(HttpStatus.NOT_FOUND, "RETURN_ROUTE_DAY_NOT_FOUND", "현재 진행 일차의 귀국 루트 데이터를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다."),
	CITY_NOT_MATCHED(HttpStatus.INTERNAL_SERVER_ERROR, "SLEEP_500_001", "시차에 매칭되는 도시를 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
