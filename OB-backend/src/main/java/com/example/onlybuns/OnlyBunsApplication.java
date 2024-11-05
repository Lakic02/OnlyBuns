package com.example.onlybuns;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class OnlyBunsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlyBunsApplication.class, args);
	}

}
