CREATE TABLE sensors_data
(
    id                BIGINT NOT NULL,
    soil_moisture     DOUBLE PRECISION,
    water_temperature DOUBLE PRECISION,
    collected_at      TIMESTAMP with time zone,
    temperature       DOUBLE PRECISION,
    humidity          DOUBLE PRECISION,
    co2               DOUBLE PRECISION,
    CONSTRAINT pk_sensorsdata PRIMARY KEY (id)
);
CREATE TABLE culture_data
(
    id                           BIGINT NOT NULL,
    plant_name                   VARCHAR,
    humidity_share               DOUBLE PRECISION,
    temperature                  DOUBLE PRECISION,
    watering_daily_frequency     DOUBLE PRECISION,
    soil_type                    VARCHAR,
    light_exposure_seconds       INTEGER,
    fertilization_schedule       VARCHAR,
    light_exposure_pause_seconds INTEGER,
    watering_seconds             INTEGER,
    watering_pause_seconds       INTEGER,
    CONSTRAINT pk_culturedata PRIMARY KEY (id)
);

CREATE TABLE schedule
(
    id              BIGINT                 NOT NULL,
    time            time WITHOUT TIME ZONE NOT NULL,
    culture_data_id BIGINT,
    CONSTRAINT pk_schedule PRIMARY KEY (id)
);

ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_CULTURE_DATA FOREIGN KEY (culture_data_id) REFERENCES culture_data (id);

