package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.service.RequestSchedulerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {

    private final RequestSchedulerService requestSchedulerService;

    public ScheduleController(RequestSchedulerService requestSchedulerService) {
        this.requestSchedulerService = requestSchedulerService;
    }

    @PostMapping("/cancel")
//    @PostMapping("/cancel/{plantId}")
    public void cancelSchedule(
//            @PathVariable("plantId") Long plantId
    ) {
        requestSchedulerService.cancelTasks();
    }

    @PostMapping("/resume")
//    @PostMapping("/resume/{plantId}")
    public void resumeSchedule(
//            @PathVariable("plantId") Long plantId
    ) {
        requestSchedulerService.resumeTasks();
    }
}
