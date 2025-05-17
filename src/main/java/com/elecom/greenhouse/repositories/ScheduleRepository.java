package com.elecom.greenhouse.repositories;

import com.elecom.greenhouse.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}