package com.example.onlybuns;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer; !!! OSTAVITI ZA TESTIRANJE !!!

@SpringBootApplication
@OpenAPIDefinition
@EnableScheduling
@EnableCaching
//@EnableEurekaServer !!! OSTAVITI ZA TESTIRANJE !!!
public class OnlyBunsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlyBunsApplication.class, args);
	}

}
