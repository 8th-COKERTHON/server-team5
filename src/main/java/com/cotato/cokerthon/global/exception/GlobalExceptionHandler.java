package com.cotato.cokerthon.global.exception;

import com.cotato.cokerthon.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		log.warn("Business exception occurred. code={}, status={}, message={}",
			errorCode.getCode(),
			errorCode.getStatus().value(),
			exception.getMessage());

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode.getCode(), exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.orElse(ErrorCode.INVALID_INPUT.getMessage());

		log.warn("Validation exception occurred. code={}, status={}, message={}",
			ErrorCode.INVALID_INPUT.getCode(),
			ErrorCode.INVALID_INPUT.getStatus().value(),
			message);

		return ResponseEntity
			.status(ErrorCode.INVALID_INPUT.getStatus())
			.body(ErrorResponse.of(ErrorCode.INVALID_INPUT.getCode(), message));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception
	) {
		log.warn("Request body parsing failed. code={}, status={}, message={}",
			ErrorCode.INVALID_SLEEP_TIME_FORMAT.getCode(),
			ErrorCode.INVALID_SLEEP_TIME_FORMAT.getStatus().value(),
			exception.getMessage());

		return ResponseEntity
			.status(ErrorCode.INVALID_SLEEP_TIME_FORMAT.getStatus())
			.body(ErrorResponse.of(
				ErrorCode.INVALID_SLEEP_TIME_FORMAT.getCode(),
				ErrorCode.INVALID_SLEEP_TIME_FORMAT.getMessage()
			));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
		log.error("Unexpected exception occurred.", exception);

		return ResponseEntity
			.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(ErrorResponse.of(
				ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
				ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
			));
	}
}
