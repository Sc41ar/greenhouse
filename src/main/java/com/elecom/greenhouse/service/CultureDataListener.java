package com.elecom.greenhouse.service;


import com.elecom.greenhouse.entities.CultureData;
import jakarta.persistence.PostUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;


public class CultureDataListener {


    private final RequestSchedulerService requestSchedulerService;

    public CultureDataListener(RequestSchedulerService requestSchedulerService) {
        this.requestSchedulerService = requestSchedulerService;
    }

    @PostUpdate
    public void postUpdate(CultureData cultureData) {
        requestSchedulerService.scheduleLightFirstRequest(cultureData.getId(), Instant.now());
    }
}
