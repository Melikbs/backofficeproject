package com.example.BackOfficeProject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.BackOfficeProject.model")
@EnableJpaRepositories(basePackages = "com.example.BackOfficeProject.repositories")
public class BackOfficeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackOfficeProjectApplication.class, args);
	}

}




