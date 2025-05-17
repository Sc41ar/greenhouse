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
                .findById(cultureDataChangeRequest.getPlantId())
                .orElseGet(() -> new CultureData());
        cultureData.setId(cultureDataChangeRequest.getPlantId());
        cultureData.setLightExposureSeconds(cultureDataChangeRequest.getLightExposure());
        cultureData.setLightExposurePauseSeconds(cultureDataChangeRequest.getLightExposurePause());
        cultureDataRepository.save(cultureData);
        return cultureDataChangeRequest;
    }

    public CultureDataChangeRequest getCultureData(long plantId) {
        CultureData cultureData = cultureDataRepository.getReferenceById(plantId);
        CultureDataChangeRequest cultureDataChangeRequest = new CultureDataChangeRequest();
        cultureDataChangeRequest.setPlantId(plantId);
        cultureDataChangeRequest.setLightExposure(cultureData.getLightExposureSeconds());
        cultureDataChangeRequest.setLightExposurePause(cultureData.getLightExposurePauseSeconds());
        return cultureDataChangeRequest;
    }
}
