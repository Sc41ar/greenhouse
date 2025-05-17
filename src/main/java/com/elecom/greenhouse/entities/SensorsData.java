package com.elecom.greenhouse.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class SensorsData {

    @Column(name = "soil_moisture")
    private Double soilMoisture = 0.0;

    @Column(name = "water_temperature")
    private Double waterTemperature = 0.0;

    @UpdateTimestamp
    @Column(name = "collected_at")
    private Instant collectedAt = Instant.now();

    @Column(name = "temperature")
    private Double temperature = 0.0;

    @Column(name = "humidity")
    private Double humidity = 0.0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id = 0L;

    @Column(name = "co2")
    private Double co2 = 0.0;
}
