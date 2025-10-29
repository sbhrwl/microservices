package com.apexsphere.hes_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class HESsimulatorApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HESsimulatorApplication.class);
		app.setWebApplicationType(WebApplicationType.SERVLET);
		app.run(args);
	}

}
