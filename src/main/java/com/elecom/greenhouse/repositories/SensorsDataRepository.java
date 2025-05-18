package com.elecom.greenhouse.repositories;

import com.elecom.greenhouse.entities.SensorsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorsDataRepository extends JpaRepository<SensorsData, Long> {
    Optional<SensorsData> findFirstByOrderByCollectedAtDesc();
}