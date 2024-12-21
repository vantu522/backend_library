package com.backend.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.backend.management.repository")
public class ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
	}
}