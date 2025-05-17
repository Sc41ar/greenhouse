package com.elecom.greenhouse.service;

import com.elecom.greenhouse.entities.CultureData;
import com.elecom.greenhouse.model.dto.CultureDataChangeRequest;
import com.elecom.greenhouse.repositories.CultureDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CultureService {


    private final CultureDataRepository cultureDataRepository;

    public CultureDataChangeRequest changeCultureData(
            CultureDataChangeRequest cultureDataChangeRequest) {
        CultureData cultureData = cultureDataRepository
                .findFirstByOrderByUpdatedAtDesc()
                .orElseGet(() -> new CultureData());
//        cultureData.setId(cultureDataChangeRequest.getPlantId());
        cultureData.setLightExposureSeconds(cultureDataChangeRequest.getLightExposure());
        cultureData.setLightExposurePauseSeconds(cultureDataChangeRequest.getLightExposurePause());
        cultureData.setWateringSeconds(cultureDataChangeRequest.getWatering());
        cultureData.setWateringPauseSeconds(cultureDataChangeRequest.getWateringPause());
        cultureDataRepository.save(cultureData);
        return cultureDataChangeRequest;
    }

    public CultureDataChangeRequest getCultureData(long plantId) {
        CultureData cultureData =
                cultureDataRepository.findFirstByOrderByUpdatedAtDesc()
                                     .orElseGet(() -> new CultureData());
        CultureDataChangeRequest cultureDataChangeRequest = new CultureDataChangeRequest();
        cultureDataChangeRequest.setPlantId(plantId);
        cultureDataChangeRequest.setLightExposure(cultureData.getLightExposureSeconds());
        cultureDataChangeRequest.setLightExposurePause(cultureData.getLightExposurePauseSeconds());
        cultureDataChangeRequest.setWatering(cultureData.getWateringSeconds());
        cultureDataChangeRequest.setWateringPause(cultureData.getWateringPauseSeconds());
        return cultureDataChangeRequest;
    }
}
