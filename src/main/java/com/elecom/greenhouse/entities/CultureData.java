package com.elecom.greenhouse.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CultureData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "plant_name")
    private String plantName;


    @Column(name = "humidity_share")
    private Double humidityShare;

    @Column(name = "soil_moisture")
    private Double soilMoisture;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "watering_daily_frequency")
    private Double wateringDailyFrequency;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "culture_data_id")
    private Set<Schedule> schedules = new LinkedHashSet<>();

    @Column(name = "soil_type")
    private String soilType;

    @Column(name = "light_exposure_seconds")
    private Integer lightExposureSeconds;

    @Column(name = "fertilization_schedule")
    private String fertilizationSchedule;

    @Column(name = "light_exposure_pause_seconds")
    private Integer lightExposurePauseSeconds;

    @Column(name = "watering_seconds")
    private Integer wateringSeconds;

    @Column(name = "watering_pause_seconds")
    private Integer wateringPauseSeconds;

}
