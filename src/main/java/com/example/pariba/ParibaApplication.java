package com.example.pariba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParibaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParibaApplication.class, args);
	}

}
