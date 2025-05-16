package com.elecom.greenhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GreenhouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenhouseApplication.class, args);
	}

}
