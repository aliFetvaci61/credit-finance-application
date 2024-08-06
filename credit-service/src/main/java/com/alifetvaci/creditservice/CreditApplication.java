package com.alifetvaci.creditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CreditApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditApplication.class, args);
	}

}
