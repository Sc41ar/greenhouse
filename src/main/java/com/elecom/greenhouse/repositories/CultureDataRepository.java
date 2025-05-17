package com.elecom.greenhouse.repositories;

import com.elecom.greenhouse.entities.CultureData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CultureDataRepository extends JpaRepository<CultureData, Long> {
    Optional<CultureData> findByPlantName(String plantName);
}