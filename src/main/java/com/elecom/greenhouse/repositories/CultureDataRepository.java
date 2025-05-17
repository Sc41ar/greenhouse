package com.elecom.greenhouse.repositories;

import com.elecom.greenhouse.entities.CultureData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultureDataRepository extends JpaRepository<CultureData, Long> {
}