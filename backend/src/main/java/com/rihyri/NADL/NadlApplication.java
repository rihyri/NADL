package com.rihyri.NADL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NadlApplication {

	public static void main(String[] args) {
		SpringApplication.run(NadlApplication.class, args);
	}

}
