package com.elecom.greenhouse.repositories;

import com.elecom.greenhouse.entities.SensorsData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorsDataRepository extends JpaRepository<SensorsData, Long> {
    SensorsData findFirstByOrderByCollectedAtDesc();
}