package com.elecom.greenhouse.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ModelResponse {
    private String plantName;
    private String article;
    private double humidityShare;
    private double soilMoisture;
    private double temperature;
    private double wateringFrequency;
    private String soilType;
    private int lightExposure;
    private int lightExposurePause;
}
