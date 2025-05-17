package com.elecom.greenhouse.model.dto;

import lombok.Data;

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
    private int wateringSeconds;
    private int wateringPause;
}
