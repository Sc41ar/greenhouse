package com.elecom.greenhouse.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FlywayConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.flyway.locations}")
    private String migrationPath;


    @Bean
    @Primary
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                              .dataSource(dbUrl, dbUser, dbPassword)
                              .locations(migrationPath)
                              .schemas("public")
                              .baselineOnMigrate(true)
                              .load();
        flyway.migrate();
        return flyway;
    }
}
