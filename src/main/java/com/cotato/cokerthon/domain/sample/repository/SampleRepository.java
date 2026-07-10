package com.cotato.cokerthon.domain.sample.repository;

import com.cotato.cokerthon.domain.sample.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, Long> {
}
