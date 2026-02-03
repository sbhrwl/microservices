package com.org.messagegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetMessage {

	public static void main(String[] args) {
		SpringApplication.run(GetMessage.class, args);
	}

}

// Go to browser and type
// localhost:9999/message/generate