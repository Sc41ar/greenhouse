package com.elecom.greenhouse.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class GreenhouseParams {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "humidity_1")
    private Integer humidity1;

    @Column(name = "humidity_2")
    private Integer humidity2;

}
