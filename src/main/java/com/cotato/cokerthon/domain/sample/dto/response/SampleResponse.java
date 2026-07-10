package com.cotato.cokerthon.domain.sample.dto.response;

import com.cotato.cokerthon.domain.sample.entity.Sample;
import java.time.LocalDateTime;

public record SampleResponse(
	Long id,
	String title,
	String content,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static SampleResponse from(Sample sample) {
		return new SampleResponse(
			sample.getId(),
			sample.getTitle(),
			sample.getContent(),
			sample.getCreatedAt(),
			sample.getUpdatedAt()
		);
	}
}
