package com.cotato.cokerthon.domain.sleep.repository;

import com.cotato.cokerthon.domain.sleep.entity.GuestUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestUsageRepository extends JpaRepository<GuestUsage, Long> {

	boolean existsByDeviceId(String deviceId);
}
