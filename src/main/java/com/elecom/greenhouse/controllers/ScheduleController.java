package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.service.RequestSchedulerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/schedule")
public class ScheduleController {

    private final RequestSchedulerService requestSchedulerService;

    public ScheduleController(RequestSchedulerService requestSchedulerService) {
        this.requestSchedulerService = requestSchedulerService;
    }

    @PostMapping("/cancel/{plantId}")
    public void cancelSchedule(
            @PathVariable("plantId") Long plantId
    ){
        requestSchedulerService.cancelTasks(plantId);
    }
}
