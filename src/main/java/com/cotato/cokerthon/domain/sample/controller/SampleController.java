package com.cotato.cokerthon.domain.sample.controller;

import com.cotato.cokerthon.domain.sample.dto.request.SampleCreateRequest;
import com.cotato.cokerthon.domain.sample.dto.response.SampleResponse;
import com.cotato.cokerthon.domain.sample.service.SampleService;
import com.cotato.cokerthon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

	private final SampleService sampleService;

	public SampleController(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	@Operation(summary = "샘플 생성", description = "기본 도메인 구조 확인용 생성 API입니다.")
	@PostMapping
	public ApiResponse<SampleResponse> createSample(@Valid @RequestBody SampleCreateRequest request) {
		return ApiResponse.ok(sampleService.createSample(request));
	}

	@Operation(summary = "샘플 목록 조회", description = "기본 도메인 구조 확인용 목록 API입니다.")
	@GetMapping
	public ApiResponse<List<SampleResponse>> getSamples() {
		return ApiResponse.ok(sampleService.getSamples());
	}

	@Operation(summary = "샘플 단건 조회", description = "기본 도메인 구조 확인용 단건 API입니다.")
	@GetMapping("/{sampleId}")
	public ApiResponse<SampleResponse> getSample(@PathVariable Long sampleId) {
		return ApiResponse.ok(sampleService.getSample(sampleId));
	}
}
