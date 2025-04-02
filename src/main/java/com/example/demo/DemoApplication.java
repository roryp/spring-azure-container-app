package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    com.azure.spring.cloud.autoconfigure.implementation.context.AzureTokenCredentialAutoConfiguration.class
})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}