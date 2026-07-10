package com.cotato.cokerthon.global.response;

public record ApiResponse<T>(
	boolean success,
	String message,
	T data
) {

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, "요청에 성공했습니다.", data);
	}

	public static ApiResponse<Void> ok() {
		return new ApiResponse<>(true, "요청에 성공했습니다.", null);
	}
}
