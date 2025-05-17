package com.elecom.greenhouse.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CultureDataChangeRequest {
    @NotNull
    private long plantId;
    @NotNull
    private int lightExposure;
    @NotNull
    private int lightExposurePause;
    @NotNull
    private int watering;
    @NotNull
    private int wateringPause;
}
