CREATE TABLE greenhouse_params
(
    id         BIGINT NOT NULL,
    humidity_1 INTEGER,
    humidity_2 INTEGER,
    CONSTRAINT pk_greenhouseparams PRIMARY KEY (id)
);

CREATE TABLE culture_data
(
    id                       BIGINT NOT NULL,
    plant_name               VARCHAR,
    humidity_share           DOUBLE PRECISION,
    temperature              DOUBLE PRECISION,
    watering_daily_frequency DOUBLE PRECISION,
    soil_type                VARCHAR,
    light_exposure_hours     INTEGER,
    fertilization_schedule   VARCHAR,
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