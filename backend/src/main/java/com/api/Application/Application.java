package com.api.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "security.filters", "security", "service", "util"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}



