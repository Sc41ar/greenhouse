package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.entities.SensorsData;
import com.elecom.greenhouse.service.SensorsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensors")
public class SensorsDataController {

    private final SensorsService sensorsService;

    public SensorsDataController(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @GetMapping("/data")
    public SensorsData getSensorsData() {
        return sensorsService.getLastSensorsData();
    }

}
