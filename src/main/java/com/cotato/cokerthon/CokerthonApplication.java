package com.cotato.cokerthon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CokerthonApplication {

	public static void main(String[] args) {
		SpringApplication.run(CokerthonApplication.class, args);
	}

}
