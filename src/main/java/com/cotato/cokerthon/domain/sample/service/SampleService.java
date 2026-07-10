package com.cotato.cokerthon.domain.sample.service;

import com.cotato.cokerthon.domain.sample.dto.request.SampleCreateRequest;
import com.cotato.cokerthon.domain.sample.dto.response.SampleResponse;
import com.cotato.cokerthon.domain.sample.entity.Sample;
import com.cotato.cokerthon.domain.sample.repository.SampleRepository;
import com.cotato.cokerthon.global.exception.BusinessException;
import com.cotato.cokerthon.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SampleService {

	private final SampleRepository sampleRepository;

	public SampleService(SampleRepository sampleRepository) {
		this.sampleRepository = sampleRepository;
	}

	@Transactional
	public SampleResponse createSample(SampleCreateRequest request) {
		Sample sample = Sample.create(request.title(), request.content());
		return SampleResponse.from(sampleRepository.save(sample));
	}

	public List<SampleResponse> getSamples() {
		return sampleRepository.findAll()
			.stream()
			.map(SampleResponse::from)
			.toList();
	}

	public SampleResponse getSample(Long sampleId) {
		Sample sample = sampleRepository.findById(sampleId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		return SampleResponse.from(sample);
	}
}
