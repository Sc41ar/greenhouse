package com.elecom.greenhouse.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class SensorsData {

    @Column(name = "soil_moisture")
    private Double soilMoisture;

    @Column(name = "water_temperature")
    private Double waterTemperature;

    @UpdateTimestamp
    @Column(name = "collected_at")
    private Instant collectedAt;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;



    @Column(name = "co2")
    private Double co2;
}
