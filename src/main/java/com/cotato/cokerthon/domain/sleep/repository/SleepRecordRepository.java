package com.cotato.cokerthon.domain.sleep.repository;

import com.cotato.cokerthon.domain.sleep.entity.SleepRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepRecordRepository extends JpaRepository<SleepRecord, Long> {
}
