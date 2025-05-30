package com.example.backofficeproject;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.example.backofficeproject.model")
@EnableJpaRepositories(basePackages = "com.example.backofficeproject.repositories")
@EnableScheduling
public class BackOfficeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackOfficeProjectApplication.class, args);
	}

}




