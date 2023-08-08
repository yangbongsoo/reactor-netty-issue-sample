package com.reproduce.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorResourceFactory;

@SpringBootApplication
public class SampleApplication {

	@Bean
	ReactorResourceFactory reactorResourceFactory() {
		return new ReactorResourceFactory();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

}
