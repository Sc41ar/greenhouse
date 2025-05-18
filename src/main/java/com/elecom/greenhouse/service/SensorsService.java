package com.elecom.greenhouse.service;


import com.elecom.greenhouse.entities.SensorsData;
import com.elecom.greenhouse.repositories.SensorsDataRepository;
import org.springframework.stereotype.Service;

@Service
public class SensorsService {

    private final SensorsDataRepository sensorsDataRepository;

    public SensorsService(SensorsDataRepository sensorsDataRepository) {
        this.sensorsDataRepository = sensorsDataRepository;
    }

    public SensorsData getLastSensorsData() {
        return sensorsDataRepository.findFirstByOrderByCollectedAtDesc()
                                    .orElseGet(() -> new SensorsData());
    }

}
